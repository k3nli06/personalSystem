package com.k3nli.personalSystem;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.k3nli.personalSystem.persistence.entity.Personal;
import com.k3nli.personalSystem.persistence.entity.TimeRegistry;
import com.k3nli.personalSystem.persistence.repository.PersonalRepository;
import com.k3nli.personalSystem.persistence.repository.TimeRegistryRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class TimeRegistryRepositoryTest {

    @Autowired
    TimeRegistryRepository timesRep;
    @Autowired
    PersonalRepository personalRep;

    Long id;
    UUID t1,t2;

    @BeforeEach
    void setup() {
        Personal personal = personalRep.save(new Personal(null, "personal", null, null, null, null));
        id = personal.getId();
        t1 = timesRep.save(new TimeRegistry(null, personal, 
                LocalDateTime.of(2025, 11, 2, 9, 0, 0), LocalDateTime.of(2025, 11, 2, 12, 0, 0))).getId();
        t2 = timesRep.save(new TimeRegistry(null, personal, 
                LocalDateTime.of(2025, 11, 3, 9, 0, 0), LocalDateTime.of(2025, 11, 3, 12, 0, 0))).getId();
    }

    @AfterEach
    void setDown() {
        personalRep.deleteById(id);
    }

    @Test
    void findByPersonalIdAndInHourBetweenTest() {

        List<?> times = timesRep.findByPersonalIdAndInHourBetween(id,
                LocalDateTime.of(2025, 11, 1, 0, 0), LocalDateTime.of(2025, 11, 30, 0, 0));
        assertEquals(times.size(), 2);

    }

    @Test
    void FindByPersonalIdAndInHoutAfterTest() {

        List<?> time = timesRep.findByPersonalIdAndInHourAfter(id, LocalDateTime.of(2025, 11, 3, 0, 0));
        assertNotNull(time.getFirst());

    }

}
