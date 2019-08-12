package com.wrial.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
/*
    使用ffmpeg进行格式转化和视频音频合并（不能直接合并，踩坑，直接合并的前提是无声）
    1.先消去原来的音
    2.在合并新的音频
 */


public class MergeVideoMp3 {

    private String ffmpegEXE;

    public MergeVideoMp3(String ffmpegEXE) {
        super();
        this.ffmpegEXE = ffmpegEXE;
    }

    public void convertor(String videoInputPath, String mp3InputPath,
                          double seconds, String videoOutputPath) throws Exception {
//		ffmpeg.exe -i xxx.mp4 -i bgm.mp3 -t 7 -y 新的视频.mp4
        List<String> command = new ArrayList<>();
        command.add(ffmpegEXE);

        command.add("-i");
        command.add(videoInputPath);

        command.add("-i");
        command.add(mp3InputPath);

        command.add("-t");
        command.add(String.valueOf(seconds));

        command.add("-y");
        command.add(videoOutputPath);

//		for (String c : command) {
//			System.out.print(c + " ");
//		}

        ProcessBuilder builder = new ProcessBuilder(command);
        Process process = builder.start();

        InputStream errorStream = process.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
        BufferedReader br = new BufferedReader(inputStreamReader);

        String line = "";
        while ((line = br.readLine()) != null) {
        }

        if (br != null) {
            br.close();
        }
        if (inputStreamReader != null) {
            inputStreamReader.close();
        }
        if (errorStream != null) {
            errorStream.close();
        }

    }

    public static void main(String[] args) {
        MergeVideoMp3 ffmpeg = new MergeVideoMp3("D:\\我的软件\\ffmpeg\\ffmpeg\\bin\\ffmpeg.exe");
        try {
            ffmpeg.convertor("C:\\Users\\weiao\\Music\\MV\\1.mp4", "C:\\Users\\weiao\\Music\\qys.mp3", 7.1, "C:\\Users\\weiao\\Music\\这是通过java生产的视频.mp4");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
