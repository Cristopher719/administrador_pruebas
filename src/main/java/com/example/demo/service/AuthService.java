package com.example.demo.service;

import com.example.demo.model.Usuario;
import com.example.demo.repository.EstudianteRepository;
import com.example.demo.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EstudianteRepository estudianteRepository;

    public Optional<Usuario> login(String username, String password) {
        return usuarioRepository.findByUsername(username)
                .filter(u -> u.getPassword().equals(password));
    }

    public void guardarSesion(HttpSession session, Usuario usuario) {
        session.setAttribute("usuarioId", usuario.getId());
        session.setAttribute("usuarioUsername", usuario.getUsername());
        session.setAttribute("usuarioRol", usuario.getRol().name());

        // Si es ESTUDIANTE buscar su entidad por usuario_id
        if (usuario.getRol() == Usuario.Rol.ESTUDIANTE) {
            estudianteRepository.findByUsuario_Id(usuario.getId())
                    .ifPresent(e -> session.setAttribute("estudianteId", e.getId()));
        }
    }

    public boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("usuarioId") != null;
    }

    public String getRol(HttpSession session) {
        Object rol = session.getAttribute("usuarioRol");
        return rol != null ? rol.toString() : null;
    }

    public Long getUsuarioId(HttpSession session) {
        Object id = session.getAttribute("usuarioId");
        return id != null ? (Long) id : null;
    }

    public Long getEstudianteId(HttpSession session) {
        Object id = session.getAttribute("estudianteId");
        return id != null ? (Long) id : null;
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }
}
