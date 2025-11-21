package xyz.ororigin.quantula.data.files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import xyz.ororigin.quantula.data.files.structure.ConfigData;
import xyz.ororigin.quantula.data.files.structure.MileStone;
import xyz.ororigin.quantula.data.files.structure.ResearchData;
import xyz.ororigin.quantula.data.files.structure.ResearchNode;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataFileManager {
    public static final String dataPath = "quantula";
    public static Logger LOGGER = LogUtils.getLogger();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private File researchDataFile;
    private File configDataFile;

    public DataFileManager() {
        LOGGER.info("[Quantula]正在初始化文件系统");
        researchDataFile = new File(dataPath + "/research.json");
        configDataFile = new File(dataPath + "/config.json");

        File parentDir = researchDataFile.getParentFile();
        if (!parentDir.exists()) {
            boolean dirsCreated = parentDir.mkdirs();
            if (!dirsCreated) {
                LOGGER.error("[Quantula]创建数据文件目录时出错");
            }
        }

        initializeFile(researchDataFile);
        boolean isConfigDataFileExist = initializeFile(configDataFile);
        if (!isConfigDataFileExist) {
            saveConfigData(new ConfigData());
        }
    }

    private boolean initializeFile(File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
                // 如果是research.json，初始化一个空的结构
                if (file.getName().equals("research.json")) {
                    ResearchData emptyData = new ResearchData();
                    emptyData.setResearch(new ArrayList<>());
                    emptyData.setLandmarks(new ArrayList<>());
                    saveResearchData(emptyData);
                }
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.error(String.format("[Quantula]创建数据文件时出错", file.getName()));
            }
            return false;
        }
        return true;
    }

    /**
     * 读取research数据
     */
    public ResearchData loadResearchData() {
        try (FileReader reader = new FileReader(researchDataFile)) {
            return GSON.fromJson(reader, ResearchData.class);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("[Quantula]读取research.json时出错");
            // 返回空的research数据
            ResearchData emptyData = new ResearchData();
            emptyData.setResearch(new ArrayList<>());
            emptyData.setLandmarks(new ArrayList<>());
            return emptyData;
        }
    }

    /**
     * 保存research数据
     */
    public void saveResearchData(ResearchData researchData) {
        try (FileWriter writer = new FileWriter(researchDataFile)) {
            GSON.toJson(researchData, writer);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("[Quantula]保存research.json时出错");
        }
    }

    /**
     * 读取配置数据
     */
    public ConfigData loadConfigData() {
        try (FileReader reader = new FileReader(configDataFile)) {
            return GSON.fromJson(reader, ConfigData.class);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("[Quantula]读取config.json时出错");
            // 返回默认配置
            return new ConfigData();
        }
    }

    /**
     * 保存配置数据
     */
    public void saveConfigData(ConfigData configData) {
        try (FileWriter writer = new FileWriter(configDataFile)) {
            GSON.toJson(configData, writer);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("[Quantula]保存config.json时出错");
        }
    }

    /**
     * 获取上次查看的X坐标
     */
    public int getPreviousXLocation() {
        return loadConfigData().getPreviousXLocation();
    }

    /**
     * 获取上次查看的Y坐标
     */
    public int getPreviousYLocation() {
        return loadConfigData().getPreviousYLocation();
    }

    /**
     * 保存查看位置
     */
    public void saveViewLocation(int x, int y) {
        ConfigData config = loadConfigData();
        config.setPreviousXLocation(x);
        config.setPreviousYLocation(y);
        saveConfigData(config);
    }

    /**
     * 获取所有research节点
     */
    public List<ResearchNode> getAllResearchNodes() {
        ResearchData researchData = loadResearchData();
        return researchData.getResearch() != null ? researchData.getResearch() : new ArrayList<>();
    }

    /**
     * 获取所有milestone
     */
    public List<MileStone> getAllMileStone() {
        ResearchData researchData = loadResearchData();
        return researchData.getMileStone() != null ? researchData.getMileStone() : new ArrayList<>();
    }

    /**
     * 根据ID查找research节点
     */
    public ResearchNode getResearchNodeById(String id) {
        List<ResearchNode> nodes = getAllResearchNodes();
        for (ResearchNode node : nodes) {
            if (node.getId() != null && node.getId().equals(id)) {
                return node;
            }
        }
        return null;
    }
}