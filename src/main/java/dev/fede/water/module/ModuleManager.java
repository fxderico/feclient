package dev.fede.water.module;
import java.util.ArrayList;
import java.util.List;
public class ModuleManager {
    public static final ModuleManager INSTANCE = new ModuleManager();
    private final List<Module> a = new ArrayList<>();
    private boolean field_b_2 = false;
    public void method_a_1(){}
    public void f(){}
    public void c(){}
    public List<Module> getModules(){return a;}
    public <T extends Module> T get(Class<T> cls){return null;}
    public Module getByName(String name){
        return a.stream().filter(m->m.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
