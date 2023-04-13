package com.mariamatvienko.olx_lab;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;

@Controller
public class HtmlPageController {

    @Autowired
    private AdvParser advParser;

    @GetMapping("/")
    public String parserForm(Model model) {
        model.addAttribute("parsingModel", new ParsingModel());
        return "main_page";
    }

    @PostMapping("/")
    public String parserFormSubmit(@ModelAttribute ParsingModel parsingModel, Model model) {
        model.addAttribute("parsingModel", parsingModel);
        return "upload_excel";
    }

    @PostMapping("/parsing")
    public ResponseEntity<ByteArrayResource> parsing(@ModelAttribute ParsingModel data) throws IOException {
        byte[] excel = createExcelTable(advParser.parse(data.getData()));
        ByteArrayResource resource = new ByteArrayResource(excel);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=advertisements.xls")
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .contentLength(excel.length)
                .body(resource);
    }

    private byte[] createExcelTable(Collection<AdvertisementInfo> advs) throws IOException {
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("advertisements");

        Row titlesRow = getRow(sheet, 0);
        titlesRow.createCell(0).setCellValue("Ідентифікатор");
        titlesRow.createCell(1).setCellValue("Ім'я");
        titlesRow.createCell(2).setCellValue("Ціна");
        titlesRow.createCell(3).setCellValue("Опубліковано");
        titlesRow.createCell(4).setCellValue("Посилання");

        int rowIndex = 1;
        for (AdvertisementInfo ad: advs) {
            Row advRow = getRow(sheet, rowIndex++);
            advRow.createCell(0).setCellValue(ad.id);
            advRow.createCell(1).setCellValue(ad.name);
            advRow.createCell(2).setCellValue(ad.price);
            advRow.createCell(3).setCellValue(ad.publish);
            advRow.createCell(4).setCellValue(ad.url);
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        workbook.write(stream);
        workbook.close();
        return stream.toByteArray();
    }

    private Row getRow(Sheet sheet, int index) {
        Row row = sheet.getRow(index);

        if (row == null) {
            return sheet.createRow(index);
        } else {
            return row;
        }
    }
}
