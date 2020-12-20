package com.leyou.service.Impl;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.leyou.controller.UploadController;
import com.leyou.service.IUploadService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


@Service
public class UploadServiceImpl implements IUploadService {

    private static final List<String> CONTENT_TYPES = Arrays.asList("image/jpeg", "image/gif","image/png");

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadController.class);
    private int index;
    private String lastString;

    //使用此客户端进行保存
    @Autowired
    private FastFileStorageClient fastFileStorageClient;


    /*
     * 图片上传的步骤：
     *  1.校验文件的类型：检查它是不是图片的类型
     *  2.校验文件的内容：防止攻击，使用 BufferredImage 读取文件的输入流，然后判空
     *  3.处理文件名：获取文件的后缀...
     *  4.保存到服务器：...
     *  5.返回图片的 url ：存储 url 到数据库中
     */
    public String upload(MultipartFile file) {

        String originalFilename = file.getOriginalFilename();
        // 校验文件的类型
        String contentType = file.getContentType();
        if (!CONTENT_TYPES.contains(contentType)){
            // 文件类型不合法，直接返回null
            LOGGER.info("文件类型不合法：{}", originalFilename);
            return null;
        }

        try {
            // 校验文件的内容
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            if (bufferedImage == null){
                LOGGER.info("文件内容不合法：{}", originalFilename);
                return null;
            }

            //文件名的处理
//            index = originalFilename.lastIndexOf(".");
//            lastString = originalFilename.substring(index);
//            UUID uuid = UUID.randomUUID();
//            originalFilename=uuid+lastString;
            String ext = StringUtils.substringAfterLast(originalFilename, ".");
            StorePath storePath = this.fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), ext, null);

            // 保存到服务器
            //file.transferTo(new File("C:\\image\\image\\" + originalFilename));
            //使用安装在 linxu 的图片服务器进行上传保存



            // 生成url地址，返回
            //return "http://image.leyou.com/" + originalFilename;
            return "http://image.leyou.com/"+storePath.getFullPath();
        } catch (IOException e) {
            LOGGER.info("服务器内部错误：{}", originalFilename);
            e.printStackTrace();
        }
        return null;
    }
}
