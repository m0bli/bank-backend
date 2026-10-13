package com.example.backend.dev;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dev")
@ConditionalOnProperty(
        name = "app.dev-reset-enabled",
        havingValue = "true"
)
public class DevResetController {

    private final JdbcTemplate jdbcTemplate;

    public DevResetController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("/reset-bazy")
    public ResponseEntity<Map<String, Object>> resetBazy() {

        // Usuwamy rekordy od tabel zależnych do nadrzędnych.
        int transakcje = jdbcTemplate.update(
                "DELETE FROM transakcje"
        );

        int konta = jdbcTemplate.update(
                "DELETE FROM konta"
        );

        int uzytkownicy = jdbcTemplate.update(
                "DELETE FROM uzytkownicy"
        );

        Map<String, Object> wynik = new LinkedHashMap<>();
        wynik.put("sukces", true);
        wynik.put("usunieteTransakcje", transakcje);
        wynik.put("usunieteKonta", konta);
        wynik.put("usunieciUzytkownicy", uzytkownicy);

        // Reset liczników wymaga uprawnienia ALTER.
        Map<String, String> liczniki = new LinkedHashMap<>();

        for (String tabela : new String[]{
                "transakcje", "konta", "uzytkownicy"
        }) {
            try {
                jdbcTemplate.execute(
                        "ALTER TABLE " + tabela
                                + " AUTO_INCREMENT = 1"
                );
                liczniki.put(tabela, "zresetowano");
            } catch (DataAccessException e) {
                liczniki.put(
                        tabela,
                        "brak uprawnien ALTER lub inny blad"
                );
            }
        }

        wynik.put("licznikiId", liczniki);

        return ResponseEntity.ok(wynik);
    }
}
