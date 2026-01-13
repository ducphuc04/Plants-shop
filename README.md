# 🌿 Plants Shop API (Backend)

> **Hệ thống Backend quản lý cửa hàng kinh doanh cây cảnh, được xây dựng theo kiến trúc Modular Monolith.**

[![Java](https://img.shields.io/badge/Java-21-orange?logo=java)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-green?logo=springboot)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-Cache-red?logo=redis)](https://redis.io/)

## 📖 Giới thiệu
**Plants Shop API** là dự án backend cung cấp các dịch vụ RESTful API phục vụ cho việc vận hành một cửa hàng cây cảnh. Hệ thống không chỉ dừng lại ở các chức năng CRUD cơ bản mà còn tập trung vào việc xử lý tính toàn vẹn dữ liệu trong các giao dịch thương mại (nhập hàng/xuất hàng) và tối ưu hóa hiệu năng.

Dự án được thiết kế theo tư duy Monolithic, giúp code dễ triển khai và thực thi.

## 🚀 Công nghệ sử dụng (Tech Stack)

* **Core:** Java 21, Spring Boot.
* **Build Tool:** Maven.
* **Database:** MySQL 8.0.44 (Lưu trữ chính), Redis (Caching & Session management).
* **Architecture:** Monolithic.
* **Containerization:** Docker (cho MySQL & Redis).

## ✨ Tính năng chính (Key Features)

### 1. Authentication & Authorization
* Cơ chế đăng nhập/đăng ký bảo mật sử dụng **JWT (JSON Web Token)**.
* Phân quyền (Role-based Authorization): Admin và User.

### 2. Nghiệp vụ Người dùng (User)
* **Shopping:** Xem danh sách cây cảnh, tìm kiếm, lọc sản phẩm.
* **Cart & Order:** Quản lý giỏ hàng, đặt hàng (Checkout).
* **History:** Xem lịch sử mua hàng, trạng thái đơn hàng.

### 3. Nghiệp vụ Quản trị (Admin)
* **Dashboard:** Biểu đồ thống kê doanh thu, số lượng đơn hàng bán ra.
* **Management:** Quản lý nhân viên, quản lý danh mục và sản phẩm (Cây cảnh).

### 4. Xử lý nâng cao (Advanced Handling)
* **Transaction Management:** Đảm bảo tính toàn vẹn dữ liệu (ACID) khi thực hiện các giao dịch phức tạp như Mua hàng (trừ kho, tạo hóa đơn, thanh toán) và Nhập hàng.
* **Data Design:** Cơ sở dữ liệu tuân thủ chuẩn hóa, đảm bảo quan hệ 1-N chặt chẽ.

## 🛠 Installation

### Prerequisites

Make sure you have installed:

- Java JDK 21
- Docker Desktop
- IntelliJ IDEA (recommended)

---

### Database & Cache Setup (Docker)

#### MySQL

```bash
docker run --name plant-mysql \
  -e MYSQL_ROOT_PASSWORD=root \
  -p 3306:3306 \
  -d mysql:8.0.44-debian

## 📂 Cấu trúc dự án (Project Structure)

Dự án được tổ chức theo hướng **Modular Monolith** (chia theo tính năng/domain) thay vì chia theo layer kỹ thuật truyền thống. Điều này giúp cô lập logic nghiệp vụ và dễ dàng tách thành Microservices nếu cần trong tương lai.

```text
src/main/java/com/Plants_shop
├── con             # Các config, utils, exception handler dùng chung
├── auth               # Module xác thực (Login, Register, JWT)
├── user               # Module quản lý thông tin người dùng
├── product            # Module quản lý sản phẩm (Cây cảnh)
├── cart               # Module giỏ hàng
├── order              # Module đơn hàng và thanh toán
└── report             # Module thống kê báo cáo (Dashboard)
