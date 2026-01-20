package com.gestionstock.backend.service;

import com.gestionstock.backend.entity.Client;
import com.gestionstock.backend.repository.ClientRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ClientService implements UserDetailsService {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder; // injecté depuis AppConfig

    public ClientService(ClientRepository clientRepository, @Lazy PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // CRUD classique
    public List<Client> listerClients() {
        return clientRepository.findAll();
    }

    public Optional<Client> findById(Long id) {
        return clientRepository.findById(id);
    }

    public Client creerClient(Client client) {
        // on encode le mot de passe avant sauvegarde
        client.setMot_de_passe(passwordEncoder.encode(client.getMot_de_passe()));
        return clientRepository.save(client);
    }

    public Client modifierClient(Long id, Client clientDetails) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client non trouvé avec l'id: " + id));

        client.setNom(clientDetails.getNom());
        client.setPrenom(clientDetails.getPrenom());
        client.setTelephone(clientDetails.getTelephone());
        client.setEtat(clientDetails.getEtat());
        // L'email ne doit généralement pas être modifié, donc on ne le touche pas.

        // On ne met à jour le mot de passe que s'il est fourni et non vide
        if (clientDetails.getMot_de_passe() != null && !clientDetails.getMot_de_passe().isBlank()) {
            client.setMot_de_passe(passwordEncoder.encode(clientDetails.getMot_de_passe()));
        }
        return clientRepository.save(client);
    }

    public void supprimerClient(Long id) {
        clientRepository.deleteById(id);
    }

    // Pour Spring Security
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // --- Utilisateur fictif pour le développement ---
        if ("phenixnguifo@gmail.com".equals(email)) {
            Client mockAdmin = new Client();
            mockAdmin.setId(999L); // ID fictif
            mockAdmin.setEmail("phenixnguifo@gmail.com");
            mockAdmin.setNom("Admin");
            mockAdmin.setPrenom("Phénix");
            // Le mot de passe brut "admin123" est encodé pour correspondre à ce que Spring Security attend.
            mockAdmin.setMot_de_passe(passwordEncoder.encode("admin123"));
            mockAdmin.setEtat("ACTIF"); // Assure que le compte est actif
            return mockAdmin;
        }
        // --- Fin de la logique pour l'utilisateur fictif ---

        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Client non trouvé avec email: " + email));
        // Client implémente UserDetails, on peut le retourner directement.
        return client;
    }

    // Pour récupérer l'entité Client complète
    public Client getClientByEmail(String email) {
        return clientRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Client non trouvé avec email: " + email));
    }
}
