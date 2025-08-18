package com.bonss.system.domain.vo;


/**
 * 说明书上传后返回实体类
 */
public class ManualVO {

    //文件名
    private String fileName;

    //原始名称
    private String originalName;

    //新文件名称
    private String newFileName;

    //文件大小，MB结尾
    private String fileSize;

    //文件格式，后缀
    private String fileForm;

    //文件上传后的url
    private String url;


    @Override
    public String toString() {
        return "ManualVO{" +
                "fileName='" + fileName + '\'' +
                ", originalName='" + originalName + '\'' +
                ", newFileName='" + newFileName + '\'' +
                ", fileSize=" + fileSize +
                ", fileForm='" + fileForm + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    public String getNewFileName() {
        return newFileName;
    }

    public void setNewFileName(String newFileName) {
        this.newFileName = newFileName;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileForm() {
        return fileForm;
    }

    public void setFileForm(String fileForm) {
        this.fileForm = fileForm;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
