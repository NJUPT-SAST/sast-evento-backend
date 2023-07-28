package sast.evento.config;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import sast.evento.annotation.OperateLog;
import sast.evento.model.Action;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/20 18:38
 */
@Slf4j
@Component
public class ActionRegister implements ApplicationListener<ContextRefreshedEvent> {

    public static Map<String, Action> actionName2action = new HashMap<>();
    public static Set<String> actionNameSet;
    private static final Boolean defaultIsPublic = false;
    private static final Boolean defaultIsVisible = true;
    private static final String PACKAGE_PATH = "classpath*:sast/evento/controller";
    private static final String host = "sast.evento";
    private static final String protocol = "http";
    private static final String defaultGroup = "default";

    @SneakyThrows
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        parseAll(getAllClass(PACKAGE_PATH));
        //默认的公开访问接口，可以在这里打开
        actionName2action.get("login").setIsPublic(true);
        actionName2action.get("post").setIsPublic(true);



        actionNameSet = actionName2action.keySet();
        log.info("Scan of action is over. Final actionName2action map is:{}", actionName2action);
    }

    @SneakyThrows
    private void parseAll(Set<Class> set) {
        for (Class c : set) {
            RequestMapping preMap = AnnotationUtils.findAnnotation(c, RequestMapping.class);
            String pre = (preMap == null) ? "" : preMap.path()[0];
            Method[] declaredMethods = c.getDeclaredMethods();
            for (Method m : declaredMethods) {
                RequestMapping r = AnnotatedElementUtils.findMergedAnnotation(m, RequestMapping.class);
                if (r == null) {
                    continue;
                }
                URL url = new URL(protocol, host, pre + r.path()[0]);
                OperateLog log = AnnotatedElementUtils.findMergedAnnotation(m, OperateLog.class);
                String description = (log == null) ? "" : log.description();
                String methodName = m.getName();
                actionName2action.put(methodName, new Action(description, methodName, r.method()[0].name(), String.valueOf(url), defaultGroup, defaultIsVisible, defaultIsPublic));
            }
        }
    }

    public Set<Class> getAllClass(String packagePath) throws IOException, ClassNotFoundException {
        Set<Class> set = new HashSet<>();
        PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = pathMatchingResourcePatternResolver.getResources(packagePath);
        for (Resource resource :
                resources) {
            URL url = resource.getURL();
            if ("file".equals(url.getProtocol())) {
                String filePath = URLDecoder.decode(url.getFile(), StandardCharsets.UTF_8);
                File dir = new File(filePath);
                List<File> files = new ArrayList<>();
                fetchFileList(dir, files);
                for (File file :
                        files) {
                    String fileAbsolutePath = file.getAbsolutePath();
                    if (fileAbsolutePath.endsWith(".class")) {
                        String noSuffixFileName = fileAbsolutePath.substring(8 + fileAbsolutePath.lastIndexOf("classes"), fileAbsolutePath.indexOf(".class"));
                        String filePackage = noSuffixFileName.replaceAll("\\\\", ".");
                        Class clazz = Class.forName(filePackage);
                        set.add(clazz);

                    }
                }
            }
        }
        return set;
    }

    private void fetchFileList(File dir, List<File> fileList) {
        if (dir.isDirectory()) {
            for (File f : Objects.requireNonNull(dir.listFiles())) {
                fetchFileList(f, fileList);
            }
        } else {
            fileList.add(dir);
        }
    }

}