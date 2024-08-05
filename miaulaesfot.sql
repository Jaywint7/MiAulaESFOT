create database miaulaesfot;
use miaulaesfot;
CREATE TABLE Aulas_Laboratorios (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    Tipo ENUM('Aula', 'Laboratorio') NOT NULL,
    Nombre VARCHAR(100) NOT NULL,
    Capacidad INT NOT NULL,
    NumeroAulLab INT NOT NULL,
    Carrera ENUM('Aguas y Saneamiento Ambiental', 'Desarrollo de Software', 'Electromenica' , 'Redes y Telecomunicaciones') NOT NULL,
    Estado ENUM('Disponible', 'Ocupado', 'Mantenimiento') NOT NULL
);
CREATE TABLE reservas (
    idR INT AUTO_INCREMENT PRIMARY KEY,
    aula_lab_id INT NOT NULL,
    usuario_id INT NOT NULL,
    fecha_reserva DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    estado ENUM('Pendiente', 'Confirmada', 'Cancelada') NOT NULL,
    FOREIGN KEY (aula_lab_id) REFERENCES aulas_laboratorios(Id)
);