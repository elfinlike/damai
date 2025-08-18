package com.bonss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 启动程序
 *
 * @author hzx
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class BonssApplication
{
    public static void main(String[] args) {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(BonssApplication.class, args);
        System.out.println("\n" +
                " ____     _____    __  __   ____     ____               ______   ____     ____    \n" +
                "/\\  _`\\  /\\  __`\\ /\\ \\/\\ \\ /\\  _`\\  /\\  _`\\            /\\  _  \\ /\\  _`\\  /\\  _`\\  \n" +
                "\\ \\ \\L\\ \\\\ \\ \\/\\ \\\\ \\ `\\\\ \\\\ \\,\\L\\_\\\\ \\,\\L\\_\\          \\ \\ \\L\\ \\\\ \\ \\L\\ \\\\ \\ \\L\\ \\\n" +
                " \\ \\  _ <'\\ \\ \\ \\ \\\\ \\ , ` \\\\/_\\__ \\ \\/_\\__ \\    _______\\ \\  __ \\\\ \\ ,__/ \\ \\ ,__/\n" +
                "  \\ \\ \\L\\ \\\\ \\ \\_\\ \\\\ \\ \\`\\ \\ /\\ \\L\\ \\ /\\ \\L\\ \\ /\\______\\\\ \\ \\/\\ \\\\ \\ \\/   \\ \\ \\/ \n" +
                "   \\ \\____/ \\ \\_____\\\\ \\_\\ \\_\\\\ `\\____\\\\ `\\____\\\\/______/ \\ \\_\\ \\_\\\\ \\_\\    \\ \\_\\ \n" +
                "    \\/___/   \\/_____/ \\/_/\\/_/ \\/_____/ \\/_____/           \\/_/\\/_/ \\/_/     \\/_/ \n" +
                "                                                                                  \n");
    }
}
