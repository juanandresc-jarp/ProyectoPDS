package com.example.nocodbdemo.service;

import com.example.nocodbdemo.model.DocumentoDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.*;
import jakarta.mail.internet.MimeMultipart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import jakarta.mail.search.FlagTerm;
import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.databind.JsonNode;



import java.io.IOException;
import java.util.Properties;

@Service
public class EmailReaderService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Value("${email.key}")
    private String emailPassword;
    @Value("${email.user}")
    private String emailUsername;

    @Autowired
    private RegistroService registroService; // el servicio que tiene createOrUpdateRegistro

    @Scheduled(fixedRate = 30000) // cada medio minuto
    public void checkInbox() {
        try {
            Session session = Session.getInstance(getMailProperties(), null);
            Store store = session.getStore("imaps");
            store.connect("imap.gmail.com", emailUsername, emailPassword);

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);

            Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));

            for (Message message : messages) {
                String body = extractContent(message);
                System.out.println("🟡 Revisando correo:");
                System.out.println(body);

                if (body != null && body.trim().startsWith("{")) {
                    try {
                        JsonNode json = objectMapper.readTree(body);

                        DocumentoDTO dto = new DocumentoDTO();
                        dto.setNombreCiudadano(json.get("nombre").asText());
                        dto.setDocumentoIdentificacion(json.get("documento").asText());
                        dto.setEmpresa(json.get("empresa").asText());

                        // Replicando comportamiento de Angular
                        String[] tiposDeQueja = {"Baja", "Media", "Critica"};
                        String tipoAleatorio = tiposDeQueja[(int)(Math.random() * tiposDeQueja.length)];
                        dto.setTipoQueja(tipoAleatorio);

                        dto.setFechaQueja(java.time.LocalDate.now().toString());
                        dto.setEstado("Pendiente");

                        registroService.createOrUpdateRegistro(dto).subscribe();

                        System.out.println("✅ Queja registrada correctamente.");
                    } catch (Exception ex) {
                        System.err.println("❌ Error al procesar el JSON:");
                        ex.printStackTrace();
                    }
                } else {
                    System.out.println("⚠️ Correo ignorado: no contiene un JSON válido.");
                }

                // Marcar como leído
                message.setFlag(Flags.Flag.SEEN, true);
            }

            inbox.close(false);
            store.close();
        } catch (Exception e) {
            System.err.println("❌ Error al conectar al correo:");
            e.printStackTrace();
        }
    }


    private Properties getMailProperties() {
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imaps");
        props.setProperty("mail.imap.ssl.enable", "true");
        return props;
    }

    private String extractContent(Part p) throws MessagingException, IOException {
        if (p.isMimeType("text/plain")) {
            return (String) p.getContent();
        } else if (p.isMimeType("multipart/*")) {
            MimeMultipart multipart = (MimeMultipart) p.getContent();
            for (int i = 0; i < multipart.getCount(); i++) {
                String result = extractContent(multipart.getBodyPart(i));
                if (result != null) return result;
            }
        }
        return null;
    }
}
