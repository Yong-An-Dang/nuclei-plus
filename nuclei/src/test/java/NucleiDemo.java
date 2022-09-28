import com.formdev.flatlaf.FlatLightLaf;
import com.g3g4x5x6.nuclei.NucleiFrame;

import javax.swing.*;

public class NucleiDemo {
    public static void main(String[] args) {
        initFlatLaf();

        SwingUtilities.invokeLater(()->{
            NucleiFrame nucleiFrame = new NucleiFrame();
            nucleiFrame.setVisible(true);
        });
    }

    private static void initFlatLaf() {
        try {
            UIManager.setLookAndFeel( new FlatLightLaf() );
        } catch( Exception ex ) {
            System.err.println( "Failed to initialize LaF" );
        }
        UIManager.put("TextComponent.arc", 5);
    }
}
