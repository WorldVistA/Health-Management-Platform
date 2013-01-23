package org.osehra.cpe;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.salt.SaltGenerator;

public class VprEncryption {

	private boolean cinit;
	private StandardPBEStringEncryptor crypto;

    public String decrypt(String str) {
    	return getCrypto().decrypt(str);
    }
    
    public String encrypt(String str) {
    	return getCrypto().encrypt(str);
    }
    
    private StandardPBEStringEncryptor getCrypto() {
    	if(!cinit) {
    		cinit();
    	}
    	return crypto;
    }

    private static byte[] salt = "HABANERO".getBytes();
    private static String password = "KOOLAID";
    private synchronized void cinit() {
    	crypto = new StandardPBEStringEncryptor();
    	crypto.setPassword(password);
    	crypto.setProvider(new BouncyCastleProvider());
    	crypto.setSaltGenerator(new SaltGenerator(){
			@Override
			public byte[] generateSalt(int lengthBytes) {
				// TODO: Derive from some other HMP configuration property that is site-specific and doesn't change (Server ID, perhaps?)
				return salt;
			}

			@Override
			public boolean includePlainSaltInEncryptionResults() {
				return false;
			}});
    	crypto.setAlgorithm("PBEWITHSHA256AND128BITAES-CBC-BC");
		cinit = true;
    }
    
    private static VprEncryption venc = null;
    
    public static VprEncryption getInstance() {
    	if(venc == null) {
    		venc = new VprEncryption();
    		venc.cinit();
    	}
    	return venc;
    }
    
    /**
     * For quick way to create encrypted values to stick directly in Properties if needed.
     * @param args
     */
    public static void main(String[] args) {
    	if(args.length>0) {
    		System.out.println(VprEncryption.getInstance().encrypt(args[0]));
    	} else {

        	final JFrame frm = new JFrame();
        	frm.setLayout(new BorderLayout());
        	final JTextField f1 = new JTextField();
        	frm.add(f1, BorderLayout.NORTH);
        	final JTextField f2 = new JTextField();
        	f2.setEditable(false);
        	frm.add(f2, BorderLayout.CENTER);
        	JButton go = new JButton("Encrypt");
        	frm.add(go, BorderLayout.SOUTH);
        	go.addActionListener(new ActionListener(){
    			@Override
    			public void actionPerformed(ActionEvent e) {
    				try {
    					f2.setText(VprEncryption.getInstance().encrypt(f1.getText()));
    					JOptionPane.showMessageDialog(frm, "Decryption check: "+VprEncryption.getInstance().decrypt(f2.getText()));
    				} catch (Exception e1) {
    					e1.printStackTrace();
    					JOptionPane.showMessageDialog(frm, e1.getMessage());
    				}
    			}
        	});
        	frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        	frm.setSize(800,120);
        	frm.setLocationRelativeTo(null);
        	frm.setVisible(true);
    	}
    }
}
