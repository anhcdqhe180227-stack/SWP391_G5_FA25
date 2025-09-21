-- Bảng Users: Chứa thông tin người dùng chung (Owner, Employee, Recep, ...)
CREATE TABLE Users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone_number VARCHAR(20),
    user_type ENUM('Owner', 'Employee', 'Recep') NOT NULL,  -- Phân biệt Owner, Employee, Recep
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bảng Owner: Chứa user_id của Owner
CREATE TABLE Owner (
    owner_id INT PRIMARY KEY,
    FOREIGN KEY (owner_id) REFERENCES Users(user_id) ON DELETE CASCADE
);

-- Bảng Employee: Chứa user_id của Employee
CREATE TABLE Employee (
    emp_id INT PRIMARY KEY,
    FOREIGN KEY (emp_id) REFERENCES Users(user_id) ON DELETE CASCADE
);

-- Bảng Recep: Chứa user_id của Recep
CREATE TABLE Recep (
    recep_id INT PRIMARY KEY,
    FOREIGN KEY (recep_id) REFERENCES Users(user_id) ON DELETE CASCADE
);

-- Bảng Vehicles: Chứa thông tin về phương tiện (vehicle)
CREATE TABLE Vehicle (
    vehicle_id INT AUTO_INCREMENT PRIMARY KEY,
    owner_id INT,
    model VARCHAR(255) NOT NULL,
    license_plate VARCHAR(20) UNIQUE NOT NULL,
    year INT,
    FOREIGN KEY (owner_id) REFERENCES Owner(owner_id) ON DELETE CASCADE
);

-- Bảng Parts: Chứa thông tin về các phụ tùng xe (phụ tùng có thể bị hỏng hoặc cần thay thế)
CREATE TABLE Parts (
    part_id INT AUTO_INCREMENT PRIMARY KEY,
    vehicle_id INT,
    expiration_date DATE,
    status ENUM('Hỏng', 'OK') NOT NULL,  -- Trạng thái của phụ tùng
    FOREIGN KEY (vehicle_id) REFERENCES Vehicle(vehicle_id) ON DELETE CASCADE
);

-- Bảng Schedule: Lịch làm việc của Employee
CREATE TABLE Schedule (
    emp_id INT,
    schedule_date DATE,
    shift ENUM('Morning', 'Afternoon', 'Night') NOT NULL,
    PRIMARY KEY (emp_id, schedule_date),
    FOREIGN KEY (emp_id) REFERENCES Employee(emp_id) ON DELETE CASCADE
);

-- Bảng Logs: Ghi lại các hoạt động của người dùng (Owner, Employee, Recep, ...)
CREATE TABLE Logs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    action VARCHAR(255) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE
);

-- Bảng Reviews: Đánh giá của Owner đối với dịch vụ
CREATE TABLE Review (
    review_id INT AUTO_INCREMENT PRIMARY KEY,
    owner_id INT,
    rating INT NOT NULL,  -- Điểm đánh giá
    comment TEXT,
    FOREIGN KEY (owner_id) REFERENCES Owner(owner_id) ON DELETE CASCADE
);

-- Bảng Appointments: Cuộc hẹn giữa Owner và Employee
CREATE TABLE Appointment (
    appointment_id INT AUTO_INCREMENT PRIMARY KEY,
    owner_id INT,
    emp_id INT,
    appointment_date DATE,
    status ENUM('Scheduled', 'Completed', 'Cancelled') DEFAULT 'Scheduled',
    FOREIGN KEY (owner_id) REFERENCES Owner(owner_id) ON DELETE CASCADE,
    FOREIGN KEY (emp_id) REFERENCES Employee(emp_id) ON DELETE CASCADE
);

-- Bảng AppointmentDetails: Chi tiết cuộc hẹn (dịch vụ, số lượng, giá cả, tổng tiền)
CREATE TABLE AppointmentDetails (
    appointment_details_id INT AUTO_INCREMENT PRIMARY KEY,
    appointment_id INT,
    quantity INT,
    service VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (appointment_id) REFERENCES Appointment(appointment_id) ON DELETE CASCADE
);

-- Bảng Invoices: Hóa đơn cho Owner sau mỗi cuộc hẹn
CREATE TABLE Invoice (
    invoice_id INT AUTO_INCREMENT PRIMARY KEY,
    owner_id INT,
    appointment_id INT,
    total_amount DECIMAL(10, 2) NOT NULL,
    issue_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (owner_id) REFERENCES Owner(owner_id) ON DELETE CASCADE,
    FOREIGN KEY (appointment_id) REFERENCES Appointment(appointment_id) ON DELETE CASCADE
);
