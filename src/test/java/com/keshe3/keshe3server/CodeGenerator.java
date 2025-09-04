package com.keshe3.keshe3server;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

public class CodeGenerator {
    public static void main(String[] args) {
        // 项目路径
        String projectPath = System.getProperty("user.dir");

        FastAutoGenerator.create("jdbc:mysql://localhost:3306/keshe3",
                        "root",
                        "root")
                .globalConfig(builder -> {
                    builder.author("CodeGenerator") // 设置作者
                            .outputDir(projectPath + "/src/main/java") // 输出目录
                            .disableOpenDir() // 生成后不打开文件夹
                            .commentDate("yyyy-MM-dd"); // 注释日期格式
                })
                .packageConfig(builder -> {
                    builder.parent("com.keshe3") // 父包名
                            .moduleName("keshe3server") // 模块名
                            .entity("entity") // 实体类包名
                            .service("service") // service包名
                            .serviceImpl("service.impl") // service实现类包名
                            .mapper("mapper") // mapper包名
                            .controller("controller"); // controller包名
                })
                .strategyConfig(builder -> {
                    builder.addInclude("task") // 设置需要生成的表名，替换为实际表名
                            .entityBuilder()
                            .enableFileOverride() // 开启文件覆盖 - 在entityBuilder中
                            .enableLombok() // 启用lombok
                            .naming(NamingStrategy.underline_to_camel) // 数据库表字段映射到实体的命名策略
                            .columnNaming(NamingStrategy.underline_to_camel)
                            .controllerBuilder()
                            .enableFileOverride() // 开启文件覆盖 - 在controllerBuilder中
                            .mapperBuilder()
                            .disableMapperXml()
                            .enableFileOverride() // 开启文件覆盖 - 在mapperBuilder中
                            .serviceBuilder()
                            .enableFileOverride() // 开启文件覆盖 - 在serviceBuilder中
                            .formatServiceFileName("I%sService") // service接口命名规则
                            .formatServiceImplFileName("%sServiceImpl"); // service实现类命名规则
                })
                .execute();
    }
}