CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- USERS
CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       first_name VARCHAR(50) NOT NULL,
                       last_name VARCHAR(50) NOT NULL,
                       username VARCHAR(100) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       is_active BOOLEAN NOT NULL
);

-- TRAINING_TYPE
CREATE TABLE training_type (
                               id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               training_type_name VARCHAR(100) NOT NULL UNIQUE
);

-- TRAINEE
CREATE TABLE trainee (
                         id UUID PRIMARY KEY,
                         date_of_birth DATE,
                         address VARCHAR(255),
                         CONSTRAINT fk_trainee_user FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE
);

-- TRAINER
CREATE TABLE trainer (
                         id UUID PRIMARY KEY,
                         specialization_id UUID NOT NULL,
                         CONSTRAINT fk_trainer_user FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE,
                         CONSTRAINT fk_trainer_specialization FOREIGN KEY (specialization_id) REFERENCES training_type(id)
);

-- TRAINER_TRAINEE
CREATE TABLE trainer_trainee (
                                 trainer_id UUID NOT NULL,
                                 trainee_id UUID NOT NULL,
                                 PRIMARY KEY (trainer_id, trainee_id),
                                 CONSTRAINT fk_tt_trainer FOREIGN KEY (trainer_id) REFERENCES trainer(id) ON DELETE CASCADE,
                                 CONSTRAINT fk_tt_trainee FOREIGN KEY (trainee_id) REFERENCES trainee(id) ON DELETE CASCADE
);

-- TRAINING
CREATE TABLE training (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          trainee_id UUID NOT NULL,
                          trainer_id UUID NOT NULL,
                          training_name VARCHAR(200) NOT NULL,
                          training_type_id UUID NOT NULL,
                          training_date DATE NOT NULL,
                          training_duration INT NOT NULL CHECK (training_duration > 0),
                          CONSTRAINT fk_training_trainee FOREIGN KEY (trainee_id) REFERENCES trainee(id) ON DELETE CASCADE,
                          CONSTRAINT fk_training_trainer FOREIGN KEY (trainer_id) REFERENCES trainer(id) ON DELETE CASCADE,
                          CONSTRAINT fk_training_type FOREIGN KEY (training_type_id) REFERENCES training_type(id)
);