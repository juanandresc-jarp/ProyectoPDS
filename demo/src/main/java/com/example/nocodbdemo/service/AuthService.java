package com.example.nocodbdemo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.nocodbdemo.dto.LoginRequest;
import com.example.nocodbdemo.dto.RegisterRequest;
import com.example.nocodbdemo.model.Usuario;
import com.example.nocodbdemo.repository.UsuarioRepository;
import com.example.nocodbdemo.security.JwtUtil;

@Service
public class AuthService {

  @Autowired
  private JwtUtil jwtUtil;

  @Autowired
  private UsuarioRepository usuarioRepo;

  @Autowired
  private PasswordEncoder passwordEncoder;

  public void register(RegisterRequest request) {
    Usuario usuario = new Usuario();
    usuario.setNombre(request.nombre);
    usuario.setEmail(request.email);
    usuario.setPassword(passwordEncoder.encode(request.password));
    usuarioRepo.save(usuario);
  }

  public String login(LoginRequest request) {
    Usuario user = usuarioRepo.findByEmail(request.email)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    if (!passwordEncoder.matches(request.password, user.getPassword())) {
      throw new RuntimeException("Contraseña incorrecta");
    }

    return jwtUtil.generateToken(user);
  }
}

