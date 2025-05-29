# 📚 OnlineLibrary – Android App (Student Project)

**OnlineLibrary** is a library management Android application created as part of a university project. The app uses **Firebase** for authentication and data storage and provides a clear distinction between **admin** and **regular users**.

---

## 🔐 Login Test Accounts

To simplify the testing process, you may use the following credentials:

### 👤 Test User  
- **Email:** `test@test.pl`  
- **Password:** `Test123`

### 🧑‍💼 Admin User  
- **Email:** `admin@admin.pl`  
- **Password:** `Admin123`

You **do not need to create a new account** unless desired. These accounts are sufficient for evaluating all core functionalities.

---

## ⚙️ Technologies Used

- **Kotlin (Android)**
- **Firebase Authentication**
- **Firebase Realtime Database**
- **Material Design UI**

> 🔐 **Security Note:**  
Firebase API key currently used in the project is publicly available for the purpose of this academic evaluation.  
**It will be disabled after the project is accepted** to comply with security best practices.

---

## 🧩 Functional Overview

### 👩‍💼 Admin Capabilities

- **Login as Admin**
- **Add new books** (Title, Author, Description, Image URL, Copies)
- **Edit existing books**
- **Loan books to users** by filtering:
  - by nickname
  - by book title
- **Mark books as returned**
- **View all active loans** (who borrowed, when)
- **View all returned loans** (who returned, when)
- **View all books** with availability
- **Navigate via admin drawer menu**

---

### 👤 User Capabilities

- **Login/Register**
- **View all books**
  - Image
  - Title
  - Author
  - Description
  - Availability
- **View active loans**
- **View returned (historical) books**
- **Edit personal information** (nickname, name, surname)
- **Navigation drawer for quick access**

---

## 🧪 Testing Steps for Teacher

### As a **User**:
1. Log in with the test user account.
2. Explore the book list.
3. View your active and returned loans.
4. Visit "Account" to update personal information.

### As an **Admin**:
1. Log in with the admin credentials.
2. Use the navigation drawer to:
   - Add new books
   - Edit existing ones
   - Loan books to users
   - Mark them as returned
   - View all loans (active & returned)

---

## 📸 Screenshots

Included in this repository are screenshots demonstrating:
- Login/Register UI
- Admin book management
- Loan processing
- User book views
- Navigation UI

### Common views

<img src="https://github.com/user-attachments/assets/5f61d73a-5f3a-48f3-8592-8c4059a04a45" width="250"/>
<img src="https://github.com/user-attachments/assets/7e00a1e7-1838-47c2-8928-b3dec2ed8bad" width="250"/>
<img src="https://github.com/user-attachments/assets/b30e3a68-bffe-4d7e-84c2-6953a2930e1d" width="250"/>

### Admin views

<img src="https://github.com/user-attachments/assets/3976b1d1-15b3-4fdc-a3c8-d5a8f23d1142" width="250"/>
<img src="https://github.com/user-attachments/assets/bdf7fabb-e155-4c2e-be9b-b461025c2bf2" width="250"/>
<img src="https://github.com/user-attachments/assets/88eb422f-884d-41c1-82e0-1c26cb25cd22" width="250"/>
<img src="https://github.com/user-attachments/assets/f668923a-5f20-44c5-b242-ca119641d3aa" width="250"/>
<img src="https://github.com/user-attachments/assets/1752867f-e25e-47e8-b6db-f357076afed7" width="250"/>
<img src="https://github.com/user-attachments/assets/67cdb32c-3450-4b12-b351-e6507d788364" width="250"/>
<img src="https://github.com/user-attachments/assets/fdbc776a-113f-446e-a4c8-0ca5b9ba0d35" width="250"/>

### User views

<img src="https://github.com/user-attachments/assets/c218160c-e36b-46d2-8a48-07fe9ae99a01" width="250"/>
<img src="https://github.com/user-attachments/assets/eb72b8bb-a077-4d47-bf7a-a9497c5429b4" width="250"/>
<img src="https://github.com/user-attachments/assets/6ba95609-0b48-47e4-b68d-0a5454ae2901" width="250"/>
<img src="https://github.com/user-attachments/assets/28f00f11-54ea-4c0f-bf0a-55a7e513057c" width="250"/>

---


