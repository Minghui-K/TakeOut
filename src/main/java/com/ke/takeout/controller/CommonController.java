package com.ke.takeout.controller;

import com.ke.takeout.common.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

/**
 * 文件上传和下载
 */
@RestController
@RequestMapping("/common")
public class CommonController {
    @Value("${TakeOut.path}")
    private String basePath;
    /**
     * 文件上传
     * @param file 必须和传入的file的name一致（可以用@ReqeustPart("file")来改）
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        //原始文件名
        String originalFilename = file.getOriginalFilename();
        //.jpg
        String extention = originalFilename.substring(originalFilename.lastIndexOf(".") - 1);
        // 使用uuid防止重复文件名覆盖前文件
        String fileName = UUID.randomUUID().toString() + extention;

        try {
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return R.success(fileName);
    }

    /**
     * 文件下载
     * @param name
     * @param response
     */
    @GetMapping("/download/{name}")
    public void download(@PathVariable String name, HttpServletResponse response) {
        response.setContentType("image/jpeg");

        File file = new File(basePath + name);

        FileInputStream fileInputStream = null;
        ServletOutputStream outputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            outputStream = response.getOutputStream();
            byte[] bytes = new byte[1024];
            while (fileInputStream.read(bytes) != -1) {
                outputStream.write(bytes);
                outputStream.flush();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }
    }
}
