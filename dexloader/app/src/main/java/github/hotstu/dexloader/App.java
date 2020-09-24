package github.hotstu.dexloader;

import android.app.Application;
import android.content.Context;

import com.android.dex.Dex;
import com.android.dx.cf.direct.DirectClassFile;
import com.android.dx.cf.direct.StdAttributeFactory;
import com.android.dx.command.dexer.DxContext;
import com.android.dx.dex.DexOptions;
import com.android.dx.dex.cf.CfOptions;
import com.android.dx.dex.cf.CfTranslator;
import com.android.dx.dex.file.DexFile;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import dalvik.system.DexClassLoader;
import dalvik.system.InMemoryDexClassLoader;

/**
 * @author hglf [hglf](https://github.com/hotstu)
 * @desc
 * @since 9/23/20
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        File f = new File(getExternalFilesDir(null), "halo.dex");
        File f2 = new File(getExternalFilesDir(null), "patch.dex");
        try {
            AssetsUtils.copyAssetsV2(this, "halo.dex", f.getAbsolutePath());
            //AssetsUtils.copyAssetsV2(this, "patch.dex", f2.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ClassLoader classLoader = getClassLoader();
        ClassLoader grandParent = classLoader.getParent();
        File halo1 = new File(getExternalCacheDir(), "halo");
        halo1.mkdirs();
        ClassLoader halo = new DexClassLoader(
                //new File(context.getApplicationInfo().sourceDir).getAbsolutePath(),
                f.getAbsolutePath()+File.pathSeparator+f2.getAbsolutePath(),
                halo1.getAbsolutePath(),
                null,
                grandParent
        );
        try {
            byte[] dump = ExpDump.dump();
            DexOptions dexOptions = new DexOptions();
            DexFile dexFile = new DexFile(dexOptions);
            DirectClassFile classFile = new DirectClassFile(dump, "github/hotstu/dexloader/Exp.class", true);
            classFile.setAttributeFactory(StdAttributeFactory.THE_ONE);
            classFile.getMagic();
            DxContext context = new DxContext();
            dexFile.add(CfTranslator.translate(context, classFile, null, new CfOptions(), dexOptions, dexFile));
            Dex dex = new Dex(dexFile.toDex(null, false));
            InMemoryDexClassLoader inMemoryDexClassLoader = new InMemoryDexClassLoader(ByteBuffer.wrap(dex.getBytes()), halo);
            ReflectUtil.setFieldNoException(ClassLoader.class, classLoader, "parent", inMemoryDexClassLoader);

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Class<?> aClass = Class.forName("github.hotstu.dexloader.Exp");
            Object o = ReflectUtil.invokeConstructor(aClass, new Class[0]);
            ReflectUtil.invoke(aClass, o, "run");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
