package com.pinyougou.shop.controller;


import com.pinyougou.entity.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import util.FastDFSClient;

@RestController

public class UploadController {

    private String FILE_SERVER_URL="http://192.168.25.133/";


    @RequestMapping("/upload")
    public Result upload( MultipartFile file) throws Exception {

        String filename = file.getOriginalFilename();

        String extname=filename.substring(filename.lastIndexOf(".")+1);

        try {
            FastDFSClient dfsClient=new FastDFSClient("classpath:config/fdfs_client.conf");

            String file1 = dfsClient.uploadFile(file.getBytes(), extname);

            String url=FILE_SERVER_URL+file1;
            System.out.println(url);

            return new Result(true,url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败");
        }

    }




}
