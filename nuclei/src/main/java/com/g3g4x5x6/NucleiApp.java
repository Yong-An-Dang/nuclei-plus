package com.g3g4x5x6;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.nuclei.DefaultTrayIcon;
import com.g3g4x5x6.nuclei.NucleiFrame;
import com.g3g4x5x6.nuclei.ultils.CheckUtil;
import com.g3g4x5x6.nuclei.NucleiConfig;
import com.g3g4x5x6.nuclei.ultils.os.OsInfoUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.RollingFileAppender;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Objects;

import static java.awt.Frame.ICONIFIED;
import static java.awt.Frame.NORMAL;


@Slf4j
public class NucleiApp {
    public static NucleiFrame nuclei;
    public static SystemTray tray;
    public static DefaultTrayIcon trayIcon;

    static {
        // Fixed: 初次启动无法找到配置文件的BUG
        String configPath = System.getProperties().getProperty("user.home") + "/.nuclei-plus/config/";
        if (!Files.exists(Path.of(configPath, "nuclei.properties"))) {
            try {
                // 创建目录
                if (!Files.exists(Path.of(configPath))) {
                    Files.createDirectories(Path.of(configPath));
                }

                // 复制配置
                InputStream nucleiIn = NucleiFrame.class.getClassLoader().getResourceAsStream("nuclei.properties");
                assert nucleiIn != null;
                Files.copy(nucleiIn, Path.of(configPath, "nuclei.properties"));
            } catch (IOException e) {
                e.fillInStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        // For Debug
        String localeStr = NucleiConfig.getProperty("nuclei.locale");
        if (!localeStr.isBlank()) Locale.setDefault(new Locale(localeStr.split("_")[0], localeStr.split("_")[1]));

        // 检查运行环境
        CheckUtil.checkEnv();

        // 加载主题
        initFlatLaf();

        // 设置 Dock 图标（部分 macOS 版本有效）
        if (Taskbar.isTaskbarSupported()) {
            Taskbar taskbar = Taskbar.getTaskbar();
            Image image = Toolkit.getDefaultToolkit().getImage(Objects.requireNonNull(NucleiApp.class.getClassLoader().getResource("icon.png")));
            try {
                taskbar.setIconImage(image);
            } catch (UnsupportedOperationException e) {
                log.warn("Dock 图标设置失败: " + e.getMessage());
            }
        }

        // Setup
        StartupFrame.setup();
    }

    public static void createGUI() {
        // 初始化日志配置
        initLogger();

        // 启动程序
        nuclei = new NucleiFrame();
        nuclei.setTitle(NucleiConfig.getProperty("nuclei.title") + " [" + NucleiConfig.projectName + "]");
        nuclei.setVisible(true);
        nuclei.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        WindowListener exitListener = new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                log.debug("关闭窗口，Windows");
                if (NucleiConfig.getProperty("nuclei.quit.to.tray").equalsIgnoreCase("false")) {
                    System.exit(0);
                } else {
                    if (OsInfoUtil.isMacOSX() || OsInfoUtil.isMacOS()) {
                        log.debug("isMacOS");
                        nuclei.setExtendedState(ICONIFIED);
                    } else {
                        nuclei.setVisible(false);
                    }
                }
            }

            @Override
            public void windowIconified(WindowEvent e) {
                log.debug("最小化窗口，Windows");
                if (NucleiConfig.getProperty("nuclei.iconified.to.tray").equalsIgnoreCase("true")) {
                    nuclei.setVisible(false);
                }
            }
        };
        nuclei.addWindowListener(exitListener);

        // 初始化系统托盘
        initSystemTray();
    }

    private static void initLogger(){
        Logger rootLogger = Logger.getRootLogger();

        // 遍历所有附加器，找到 RollingFileAppender
        Enumeration appenders = rootLogger.getAllAppenders();
        while (appenders.hasMoreElements()) {
            Object appenderObj = appenders.nextElement();
            if (appenderObj instanceof RollingFileAppender) {
                RollingFileAppender fileAppender = (RollingFileAppender) appenderObj;

                // 替换成你想要的新路径
                String newLogPath = Path.of(NucleiConfig.getWorkPath(),"logs","nuclei-plus.log").toString();
                fileAppender.setFile(newLogPath);
                fileAppender.activateOptions(); // 应用更改

                System.out.println("日志路径已更改为: " + newLogPath);
            }
        }
    }

    private static void initFlatLaf() {
        try {
            if (NucleiConfig.getProperty("nuclei.theme").isEmpty()) UIManager.setLookAndFeel(new FlatLightLaf());
            else UIManager.setLookAndFeel(NucleiConfig.getProperty("nuclei.theme"));
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }
        UIManager.put("TextComponent.arc", 5);
    }

    private static void initSystemTray() {
        /*
         * 添加系统托盘
         */
        if (SystemTray.isSupported()) {
            // 获取当前平台的系统托盘
            tray = SystemTray.getSystemTray();
            // 加载一个图片用于托盘图标的显示
            Image image = null;
            try {
                image = ImageIO.read(Objects.requireNonNull(NucleiApp.class.getClassLoader().getResource("icon.png")));
            } catch (IOException e) {
                e.fillInStackTrace();
            }

            // 创建右键图标时的弹出菜单：JPopupMenu
            JPopupMenu popupMenu = new JPopupMenu();

            JMenuItem projectMenuItem = new JMenuItem("当前项目：" + NucleiConfig.projectName);
            projectMenuItem.setIcon(new FlatSVGIcon("icons/project.svg"));

            JMenuItem openMenuItem = new JMenuItem("打开");
            openMenuItem.setIcon(new FlatSVGIcon("icons/start.svg"));
            openMenuItem.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    openApp();
                }
            });

            JMenuItem exitMenuItem = new JMenuItem("退出");
            exitMenuItem.setIcon(new FlatSVGIcon("icons/exit.svg"));
            exitMenuItem.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });

            popupMenu.add(projectMenuItem);
            popupMenu.addSeparator();
            popupMenu.add(openMenuItem);
            popupMenu.add(exitMenuItem);

            // 创建一个托盘图标
            assert image != null;
            trayIcon = getDefaultTrayIcon(image, popupMenu);

            // 添加托盘图标到系统托盘
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                e.fillInStackTrace();
            }
        }
    }

    private static @NotNull DefaultTrayIcon getDefaultTrayIcon(Image image, JPopupMenu popupMenu) {
        DefaultTrayIcon trayIcon = new DefaultTrayIcon(image, "点击打开 nuclei-plus", popupMenu);
        trayIcon.setToolTip("项目：" + NucleiConfig.projectName);

        // 托盘图标自适应尺寸
        trayIcon.setImageAutoSize(true);
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                switch (e.getButton()) {
                    case MouseEvent.BUTTON1: {
                        System.out.println("托盘图标被鼠标左键被点击");
                        openApp();
                        break;
                    }
                    case MouseEvent.BUTTON2: {
                        System.out.println("托盘图标被鼠标中键被点击");
                        break;
                    }
                    case MouseEvent.BUTTON3: {
                        System.out.println("托盘图标被鼠标右键被点击，X:" + e.getX() + " <=> Y:" + e.getY());
                        break;
                    }
                    default: {
                        break;
                    }
                }
            }
        });
        return trayIcon;
    }

    private static void openApp() {
        nuclei.setVisible(true);
        nuclei.setExtendedState(NORMAL);
    }

}
