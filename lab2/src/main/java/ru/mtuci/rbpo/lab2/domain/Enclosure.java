package ru.mtuci.rbpo.lab2.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "enclosures")
public class Enclosure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "enclosure_species", joinColumns = @JoinColumn(name = "enclosure_id"))
    @Column(name = "species", nullable = false)
    private Set<String> allowedSpecies = new LinkedHashSet<>();

    @Column(name = "capacity", nullable = false)
    private int capacity;

    protected Enclosure() {
    }

    public Enclosure(String name, Set<String> allowedSpecies, int capacity) {
        this.name = name;
        this.allowedSpecies = new LinkedHashSet<>(allowedSpecies);
        this.capacity = capacity;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getAllowedSpecies() {
        return Set.copyOf(allowedSpecies);
    }

    public void setAllowedSpecies(Set<String> allowedSpecies) {
        this.allowedSpecies = new LinkedHashSet<>(allowedSpecies);
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
