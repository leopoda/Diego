
CREATE USER 'pig'@'localhost' IDENTIFIED BY 'pig'; 
CREATE USER 'pig'@'%' IDENTIFIED BY 'pig'; 
GRANT ALL ON *.* TO 'pig'@'localhost';
GRANT ALL ON *.* TO 'pig'@'%';
FLUSH PRIVILEGES;