package com.erfan.cch.Repo;

import com.erfan.cch.Models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {}
