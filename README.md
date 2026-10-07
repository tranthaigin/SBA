# SBA301 - Bài tập, thực hành và tài liệu

Repository [tranthaigin/SBA](https://github.com/tranthaigin/SBA) tập hợp các bài đã thực hiện của môn SBA301. Các thư mục giữ nguyên tên hiện có để bảo toàn đường dẫn của từng bài.

## Danh mục dự án

| Phần | Thư mục / hướng dẫn | Nội dung |
|---|---|---|
| Slot 02 | [SLot2/sba301-learning-dashboard](SLot2/sba301-learning-dashboard/README.md) | React learning dashboard |
| Slot 03 | [Slot3/orchid-explorer](Slot3/orchid-explorer/README.md) | Orchid Explorer, React-Bootstrap |
| Slot 04 | [Slot4/interactive-orchid-explorer](Slot4/interactive-orchid-explorer/README.md) | Props, State, Hooks, Context |
| Slot 05 | [Slot5/eventhub-campus-explorer](Slot5/eventhub-campus-explorer/README.md) | EventHub Campus Explorer |
| Slot 06 | [Slot6/react-hook-product-manager](Slot6/react-hook-product-manager/README.md) | Product CRUD với React Hooks |
| Slot 07 | [Slot7/sba301-event-navigator](Slot7/sba301-event-navigator/README.md) | React Router, dynamic routes |
| Slot 08 | [Slot8/slot8-product-rest-api-lab](Slot8/slot8-product-rest-api-lab/README.md) | Product mock REST API, Postman |
| Slot 09 | [Slot9/ministore-spa](Slot9/ministore-spa/README.md) | MiniStore SPA, nested routes |
| Slot 10 | [Slot10/orchid-router-demo](Slot10/orchid-router-demo/README.md) | Orchid Router SPA |
| Slot 12 | [Slot12/slot12-rest-design](Slot12/slot12-rest-design/README.md) | REST fundamentals, Spring Boot skeleton |
| Slot 13 | [Slot13/slot13-rest-api](Slot13/slot13-rest-api/README.md) | Spring Boot REST API, kiến trúc 3 layer |
| Lab 01 | [lab1](lab1/README.md) | Orchid Gallery với Props và State |
| Lab 02 | [lab2/orchid-gallery-spa](lab2/orchid-gallery-spa/README.md) | Orchid Gallery SPA, API loading/error/empty states |
| Lab 03 | [lab3](lab3/README.md) | Employee REST API, versioning, Page/Slice, Swagger và automated testing |
| Lab 04 | [lab4](lab4/README.md) | Orchid REST API, JPA, SQL Server, CRUD/search và evidence |

Danh mục này phản ánh các thư mục thực tế trong repository, không khẳng định mọi Slot của môn học đều đã được triển khai. Các tài liệu Slot khác trong thư mục tài liệu là nội dung học tập, không phải bài làm tương ứng.

## Lab03 - REST API và testing

[Lab03](lab3/README.md) nằm ở tiết 45-46 trong syllabus; phần hướng dẫn core nằm trong Slot15/Slot16. Employee API dùng repository trong bộ nhớ, chạy riêng tại port `8083`, gồm CRUD, URI versioning, Page/Slice và Swagger. Xem [nguồn đề và phạm vi](lab3/docs/source-discovery.md), [API contract](lab3/docs/api-contract.md), [báo cáo kiểm chứng](lab3/docs/verification.md).

## Lab04 - chạy và kiểm tra

Yêu cầu JDK 21, Docker Desktop Linux engine và PowerShell. Maven Wrapper đi kèm Lab4. SQL Server của lab dùng port `14334`; API dùng port `8080`.

```powershell
cd "C:\SBA301\bài tập\sba-301\lab4"
.\mvnw.cmd -B clean package
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\init-local.ps1
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\run.ps1 -SkipBuild
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\verify-api.ps1 -VerifyDatabase -Restart
```

Chi tiết: [README Lab4](lab4/README.md), [báo cáo kiểm chứng](lab4/docs/verification.md), [test matrix](lab4/docs/test-matrix.md), [Postman collection](lab4/postman/Orchid-Lab04.postman_collection.json). Java tests sử dụng H2; bộ live HTTP, PK/FK, Unicode và restart đã được chạy riêng trên SQL Server thật. Xem báo cáo để phân biệt phạm vi của từng bằng chứng và phần người học bổ sung trước khi nộp.

## Chạy các bài khác

Làm theo README trong từng thư mục. Các bài React/Node dùng `npm.cmd` trên Windows; các bài Spring Boot dùng Maven/JDK theo hướng dẫn riêng. `launcher.js` là công cụ xem các dự án React/Node đã đăng ký:

```powershell
cd "C:\SBA301\bài tập\sba-301"
node launcher.js
```

Mở `http://localhost:3000`. Các bài Spring Boot, gồm Lab4, chạy theo README riêng.

## Tài liệu và nội dung Git

[Tài Liệu](T%C3%A0i%20Li%E1%BB%87u/) chứa PDF course/guide, gồm hai tài liệu yêu cầu Lab04. Source, assets, cấu hình dùng chung, lockfiles, tài liệu và evidence được lưu trong Git. `node_modules`, build outputs, `.env`, `.local`, log runtime và hook công cụ cục bộ được bỏ qua. Chạy lệnh cài dependency theo từng README sau khi clone; không cần đưa dependency hoặc mật khẩu vào repository.
