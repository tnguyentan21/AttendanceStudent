# AttendanceStudent

AttendanceStudent is an Android application to check attendance of students.


## Requirement

Use Android Studio 4.0 or higher.

Use Android 4.4 or higher.


## Permission

This application require permission to access `location, camera, and the rights to write to internal storage` of the device.


## Installation

Add the SHA certificate fingerprint to this [database](https://console.firebase.google.com/u/4/project/attendancestudent-3b9a6/settings/general/android:com.qc.attendancestudent) by using the account below.

```bash
account: nttdemo20@gmail.com
password: Tantai123
```
Open the project with Android Studion then run the app.
## Usage
**For teacher role, follow these steps:**
1. Login the app with this account

```python
account: nttdemo20@gmail.com
password: Tantai123
```
2. Press `LÀM MỚI DỮ LIỆU` to start with fresh data.

3. Press `Lớp CNTT 01`.

4. Press `CHIA SẼ` if you want to share the QRcode, then press "ĐÓNG".

5. Turn on `Trạng thái: ` for student to start check attendance by scan the QRcode.

6. Turn off `Trạng thái: ` to stop student from check attendance by scan the QRcode.

7. Press `XUẤT DS` to export attendance list to csv format file.

8. Press the door icon at top right corner of the application to log out.

9. Check the attendance list file at this direction
```python 
Android 10: localstorage/Android/data/com.qc.attendancestudent/files/Download/
Android 9 or lower: localstorage/Download/
```

**For student role, follow these steps:**
1. Login the app with one of these account

```python
nttdemo001@gmail.com
Tantai123456

nttdemo002@gmail.com
Tantai123

nttdemo003@gmail.com
Tantai123
```
2. Press `SCAN QR CODE` to start scan QRcode provided by teacher and check attendance.

3. Press the door icon at top right corner of the application to log out.

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
This application is a product of a group consist of following members:
```
17521000 Nguyễn Tấn Tài
17520807 Dương Hồng Ngọc
17520434 Lê Ngọc Hân
```
