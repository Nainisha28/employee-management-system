INSERT IGNORE INTO employees(employee_code,full_name,email,mobile,aadhaar,pan,date_of_birth,gender,address,department,role_name,joining_date,annual_salary,status) VALUES
('I0001','Aarav Mehta','aarav.mehta@example.com','9000000001','111122223333','ABCDE1234F','1997-03-12','Male','MP Nagar, Bhopal','IT','Software Engineer','2022-01-10',650000,'Active'),
('I0002','Priya Sharma','priya.sharma@example.com','9000000002','111122223334','BCDEF2345G','1995-07-18','Female','Arera Colony, Bhopal','IT','Team Lead','2020-04-15',1150000,'Active'),
('I0003','Kabir Singh','kabir.singh@example.com','9000000003','111122223335','CDEFG3456H','1998-11-02','Male','Kolar Road, Bhopal','IT','Software Engineer','2023-06-01',480000,'Active'),
('I0004','Ananya Rao','ananya.rao@example.com','9000000004','111122223336','DEFGH4567J','1996-02-24','Female','Shahpura, Bhopal','IT','Software Engineer','2021-09-20',720000,'Inactive'),
('I0005','Ishaan Verma','ishaan.verma@example.com','9000000005','111122223337','EFGHI5678K','1994-09-09','Male','Bawadia Kalan, Bhopal','IT','Team Lead','2019-03-11',1450000,'Active'),
('H0001','Neha Joshi','neha.joshi@example.com','9000000006','111122223338','FGHIJ6789L','1997-05-16','Female','TT Nagar, Bhopal','HR','HR Executive','2022-08-01',360000,'Active'),
('H0002','Rohan Patel','rohan.patel@example.com','9000000007','111122223339','GHIJK7890M','1993-12-05','Male','Ayodhya Bypass, Bhopal','HR','HR Executive','2020-02-17',520000,'Active'),
('H0003','Meera Nair','meera.nair@example.com','9000000008','111122223340','HIJKL8901N','1999-01-27','Female','Berasia Road, Bhopal','HR','HR Executive','2024-01-08',280000,'Active'),
('F0001','Dev Malhotra','dev.malhotra@example.com','9000000009','111122223341','IJKLM9012P','1992-04-14','Male','Hoshangabad Road, Bhopal','Finance','Accountant','2018-11-12',820000,'Active'),
('F0002','Simran Kaur','simran.kaur@example.com','9000000010','111122223342','JKLMN0123Q','1996-08-22','Female','Gulmohar, Bhopal','Finance','Accountant','2021-05-03',450000,'Active'),
('F0003','Aditya Jain','aditya.jain@example.com','9000000011','111122223343','KLMNO1234R','1998-10-10','Male','BHEL Township, Bhopal','Finance','Accountant','2023-03-27',310000,'Inactive'),
('S0001','Kavya Iyer','kavya.iyer@example.com','9000000012','111122223344','LMNOP2345S','1995-06-06','Female','New Market, Bhopal','Sales','Sales Executive','2020-07-13',390000,'Active'),
('S0002','Arjun Das','arjun.das@example.com','9000000013','111122223345','MNOPQ3456T','1997-12-19','Male','Raisen Road, Bhopal','Sales','Sales Executive','2022-10-10',560000,'Active'),
('S0003','Sana Khan','sana.khan@example.com','9000000014','111122223346','NOPQR4567U','1994-02-11','Female','Lalghati, Bhopal','Sales','Team Lead','2019-08-05',1050000,'Active'),
('A0001','Vikram Sethi','vikram.sethi@example.com','9000000015','111122223347','OPQRS5678V','1991-09-30','Male','Kohefiza, Bhopal','Admin','Administrator','2017-12-01',620000,'Active'),
('A0002','Pooja Desai','pooja.desai@example.com','9000000016','111122223348','PQRST6789W','1998-04-03','Female','Bhopal City','Admin','Administrator','2023-02-20',295000,'Active'),
('I0006','Rahul Sharma','rahul.sharma@example.com','9000000017','111122223349','QRSTU7890X','1995-10-28','Male','Indrapuri, Bhopal','IT','Software Engineer','2021-01-04',590000,'Active'),
('H0004','Tanya Kapoor','tanya.kapoor@example.com','9000000018','111122223350','RSTUV8901Y','1996-03-15','Female','Misrod, Bhopal','HR','HR Executive','2022-04-18',330000,'Active'),
('F0004','Manav Gupta','manav.gupta@example.com','9000000019','111122223351','STUVW9012Z','1993-07-21','Male','Govindpura, Bhopal','Finance','Accountant','2019-06-24',980000,'Active'),
('S0004','Diya Chawla','diya.chawla@example.com','9000000020','111122223352','TUVWX0123A','1999-12-31','Female','Rohit Nagar, Bhopal','Sales','Sales Executive','2024-05-06',250000,'Active');
INSERT IGNORE INTO employee_code_sequences(department,last_number) VALUES ('IT',6),('HR',4),('Finance',4),('Sales',4),('Admin',2);
INSERT INTO employee_transfer_history(employee_id,old_code,new_code,old_department,new_department,transfer_date,reason,created_at)
SELECT id,'I0006','H0004','IT','HR','2025-02-01','Sample transfer history','2025-02-01 10:00:00' FROM employees WHERE employee_code='H0004' LIMIT 1;
