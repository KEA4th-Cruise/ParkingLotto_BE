package com.cruise.parkinglotto.global.excel;

import com.cruise.parkinglotto.domain.Applicant;
import com.cruise.parkinglotto.domain.Draw;
import com.cruise.parkinglotto.global.kc.ObjectStorageConfig;
import com.cruise.parkinglotto.global.kc.ObjectStorageService;
import com.cruise.parkinglotto.repository.DrawRepository;
import com.cruise.parkinglotto.repository.ParkingSpaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileGeneration {

    private final ObjectStorageService objectStorageService;
    private final ObjectStorageConfig objectStorageConfig;
    private final DrawRepository drawRepository;
    private final ParkingSpaceRepository parkingSpaceRepository;

    public String generateAndUploadExcel(Draw draw, List<Applicant> orderedApplicants) {
        try {
            String url = generateDrawResultExcel(draw, orderedApplicants);
            draw.updateResultURL(url);
            drawRepository.save(draw);
            return url;
        } catch (IOException e) {
            log.error("Error occurred while generating or uploading Excel file for drawId: {}", draw.getId(), e);
        }
        return null;
    }

    private String generateDrawResultExcel(Draw draw, List<Applicant> orderedApplicants) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("추첨결과");

            // 데이터 형식과 스타일 설정
            DataFormat format = workbook.createDataFormat();
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setDataFormat(format.getFormat("0.0")); // 소수점 한 자리 형식
            cellStyle.setAlignment(HorizontalAlignment.CENTER); // 가운데 정렬

            Row headerRow = sheet.createRow(1);
            String[] headers = {"순번", "이름", "직종", "가중치", "당첨여부"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            int rowNum = 2;
            int i = 1;
            for (Applicant applicant : orderedApplicants) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(i);
                row.createCell(1).setCellValue(maskName(applicant.getMember().getNameKo()));
                row.createCell(2).setCellValue(applicant.getMember().getDeptPathName());

                Cell cell3 = row.createCell(3);
                cell3.setCellValue(applicant.getWeightedTotalScore());
                cell3.setCellStyle(cellStyle);

                row.createCell(4).setCellValue(applicant.getWinningStatus().toString());
                i++;
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            byte[] excelBytes = baos.toByteArray();

            // byte[]를 MultipartFile로 변환
            MultipartFile multipartFile = new ByteArrayMultipartFile("file", "draw_results_" + draw.getId() + ".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", excelBytes);

            String fileName = draw.getTitle().replace(" ", "_") + "_결과.xlsx";

            return objectStorageService.uploadObject(objectStorageConfig.getDrawResultDocument(), fileName, multipartFile);
        } catch (Exception e) {
            log.error("Error occurred while generating or uploading Excel file for drawId: {}", draw.getId(), e);
            return null;
        }
    }

    private static String maskName(String name) {
        if (name == null || name.length() < 2) {
            return name; // 이름이 너무 짧으면 마스킹하지 않음
        }
        if (name.length() == 2) {
            return name.charAt(0) + "*"; // 이름이 2글자인 경우, 맨 뒷글자만 가림
        }
        StringBuilder maskedName = new StringBuilder(name.length());
        maskedName.append(name.charAt(0)); // 첫 글자 추가
        for (int i = 1; i < name.length() - 1; i++) {
            maskedName.append('*'); // 중간 글자는 *로 대체
        }
        maskedName.append(name.charAt(name.length() - 1)); // 마지막 글자 추가
        return maskedName.toString();
    }
}