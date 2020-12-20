package com.leyou.controller;
import org.apache.commons.lang.StringUtils;
import com.leyou.service.IUploadService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("upload")
public class UploadController {

    @Autowired
    private IUploadService iUploadService;

    @PostMapping("image")
    public ResponseEntity<String> upload(@RequestParam("file")MultipartFile file)
    {
        String url = iUploadService.upload(file);
        if(StringUtils.isBlank(url))
        {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(url);
    }

}
