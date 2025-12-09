package com.gymcrm.gym_crm_spring.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "training")
public class Training {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "trainee_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Trainee trainee;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Trainer trainer;

    @Column(name = "training_name", nullable = false, length = 200)
    private String trainingName;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "training_type_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private TrainingType trainingType;

    @Column(name = "training_date", nullable = false)
    private LocalDate trainingDate;

    @Column(name = "training_duration", nullable = false)
    private Integer trainingDuration;
}
