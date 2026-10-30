package ru.mtuci.rbpo.lab4.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "adoption_applications")
public class AdoptionApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "animal_id", nullable = false)
    private Long animalId;

    @Column(name = "applicant_id", nullable = false)
    private Long applicantId;

    @Column(name = "motivation", length = 500)
    private String motivation;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private AdoptionStatus status;

    @Column(name = "submitted_at", nullable = false)
    private Instant submittedAt;

    protected AdoptionApplication() {
    }

    public AdoptionApplication(Long animalId, Long applicantId, String motivation, AdoptionStatus status,
                               Instant submittedAt) {
        this.animalId = animalId;
        this.applicantId = applicantId;
        this.motivation = motivation;
        this.status = status;
        this.submittedAt = submittedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getAnimalId() {
        return animalId;
    }

    public Long getApplicantId() {
        return applicantId;
    }

    public String getMotivation() {
        return motivation;
    }

    public void setMotivation(String motivation) {
        this.motivation = motivation;
    }

    public AdoptionStatus getStatus() {
        return status;
    }

    public void setStatus(AdoptionStatus status) {
        this.status = status;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }
}
