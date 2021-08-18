package com.xuanhuo.common.core.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.ValidateObjectInputStream;

import java.io.*;

/**
 * 序列化工具类
 */
public class SerializableUtil<T extends Serializable> {


    /**
     * 对象序列化到指定的文件中
     * @param object
     * @param path
     */
    public void serializableObjectToFile(T object, String path) {
        //文件不存在则创建
        File file = FileUtil.file(path);
        if(!FileUtil.exist(file)){
            FileUtil.touch(file);
        }
        //序列化
        try( ObjectOutputStream outputStream =  new ObjectOutputStream(new FileOutputStream(file));){
            outputStream.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从文件中进行反序列化
     * @param path
     * @return
     */
    public T deSerializableObjectFromFile(String path,Class<T>[] acceptClasses,Class<T> tClass){
        //文件不存在则创建
        File file = FileUtil.file(path);
        if(!FileUtil.exist(file)){
            return null;
        }
        T serializable = null;
        try(ValidateObjectInputStream validateObjectInputStream = new ValidateObjectInputStream(new FileInputStream(file),acceptClasses);) {
            //白名单
            serializable = IoUtil.readObj(validateObjectInputStream, tClass);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serializable;
    }
}
