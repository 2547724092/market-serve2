package com.wanli.util;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.VoidResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
@Component
@Slf4j
public class OSSUtil {
    @Value("${aliyun.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.secretAccessKey}")
    private String secretAccessKey;

    @Value("${aliyun.oss.endPoint}")
    private String endPoint;

    @Value("${aliyun.oss.bucketName}")
    private String bucketName;

    /**
     *@Description: 上传单个文件
     *@Param: [org.springframework.web.multipart.MultipartFile]
     *@return: java.util.HashMap<java.lang.String,java.lang.Object>
     */
    public String uploadOneFile(MultipartFile file) {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endPoint, accessKeyId, secretAccessKey);

        try {
            return getResult(ossClient, file);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
    /**
     *@Description: 上传多个文件
     *@Param: [org.springframework.web.multipart.MultipartFile[]]
     *@return: java.util.List<java.util.HashMap<java.lang.String,java.lang.Object>>
     */
//    public List<HashMap<String,Object>> uploadArrayFile(MultipartFile[] files){
////        创建OSSClient实例
//        OSS ossClient = new OSSClientBuilder().build(endPoint, accessKeyId, secretAccessKey);
//        List<HashMap<String, Object>> result = new ArrayList<>();
//
//        try {
//            //设置文件名
//            for (MultipartFile file : files) {
//                HashMap<String, Object> r = getResult(ossClient, file);
//                result.add(r);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        } finally {
//            if (ossClient != null) {
//                ossClient.shutdown();
//            }
//        }
//        return result;
//    }

    /**
     *@Description: 通过url删除文件
     *@Param: [java.lang.String]
     *@return: boolean
     */

    public boolean deleteFile(String fileUrl){
//        创建OSSClient实例
        OSS ossClient = new OSSClientBuilder().build(endPoint, accessKeyId, secretAccessKey);
        /** oss删除文件是根据文件完成路径删除的，但文件完整路径中不能包含Bucket名称。
         * 比如文件路径为：http://edu-czf.oss-cn-guangzhou.aliyuncs.com/2022/08/abc.jpg",
         * 则完整路径就是：2022/08/abc.jpg
         */
        int begin = ("https://" + bucketName + "." + endPoint + "/").length(); //找到文件路径的开始下标
        String deleteUrl = fileUrl.substring(begin);

        try {
            // 删除文件请求
            VoidResult voidResult = ossClient.deleteObject(bucketName, deleteUrl);

            log.info("OSS删除{}图片状态--{}",deleteUrl,voidResult.getResponse().getStatusCode());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    private String getResult(OSS ossClient, MultipartFile file) throws IOException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");  //2023/05/23jwjij393sj3jsjd3i3jjskjdkjdlksajldkas
        // 命名
        String fileName = sdf.format(new Date())
                + UUID.randomUUID().toString().replace("-", "")
                + file.getOriginalFilename();
        // 创建PutObject请求。
        ossClient.putObject(bucketName, fileName, file.getInputStream());
        StringBuilder url = new StringBuilder();
        url.append("https://")
                .append(bucketName)
                .append(".")
                .append(endPoint)
                .append("/")
                .append(fileName);

        return url.toString();  //返回就是上传成功后，在OSS第三方服务器上的 图片地址。
    }
}
