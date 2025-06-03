package istatistik;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class IstatistikArayuz extends JFrame {

    private JTextField klasorYoluField;
    private JTextArea mesajAlani;
    private JCheckBox ortalamaCheck, maxCheck, minCheck, stdCheck, freqCheck, medianCheck;
    private JCheckBox ortalamaGlobal, maxGlobal, minGlobal, stdGlobal, freqGlobal, medianGlobal;

    public IstatistikArayuz() {
        setTitle("İstatistik Analiz Uygulaması");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Üst panel (klasör seçme)
        JPanel ustPanel = new JPanel(new BorderLayout());
        klasorYoluField = new JTextField();
        JButton klasorSecBtn = new JButton("Klasör Seç");

        klasorSecBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                klasorYoluField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });

        ustPanel.add(klasorYoluField, BorderLayout.CENTER);
        ustPanel.add(klasorSecBtn, BorderLayout.EAST);

        // Orta panel (seçenekler)
        JPanel secenekPanel = new JPanel(new GridLayout(6, 2));
        ortalamaCheck = new JCheckBox("Ortalama");
        ortalamaGlobal = new JCheckBox("GLOBAL");

        maxCheck = new JCheckBox("Max");
        maxGlobal = new JCheckBox("GLOBAL");

        minCheck = new JCheckBox("Min");
        minGlobal = new JCheckBox("GLOBAL");

        stdCheck = new JCheckBox("Std Sapma");
        stdGlobal = new JCheckBox("GLOBAL");

        freqCheck = new JCheckBox("Frekans");
        freqGlobal = new JCheckBox("GLOBAL");

        medianCheck = new JCheckBox("Median");
        medianGlobal = new JCheckBox("GLOBAL");

        secenekPanel.add(ortalamaCheck);
        secenekPanel.add(ortalamaGlobal);
        secenekPanel.add(maxCheck);
        secenekPanel.add(maxGlobal);
        secenekPanel.add(minCheck);
        secenekPanel.add(minGlobal);
        secenekPanel.add(stdCheck);
        secenekPanel.add(stdGlobal);
        secenekPanel.add(freqCheck);
        secenekPanel.add(freqGlobal);
        secenekPanel.add(medianCheck);
        secenekPanel.add(medianGlobal);

        // Alt panel (hesapla butonu ve sonuç alanı)
        JPanel altPanel = new JPanel(new BorderLayout());
        JButton hesaplaBtn = new JButton("HESAPLA");
        mesajAlani = new JTextArea();
        mesajAlani.setEditable(false);
        JScrollPane scroll = new JScrollPane(mesajAlani);

        hesaplaBtn.addActionListener(e -> hesapla());

        altPanel.add(hesaplaBtn, BorderLayout.NORTH);
        altPanel.add(scroll, BorderLayout.CENTER);

        add(ustPanel, BorderLayout.NORTH);
        add(secenekPanel, BorderLayout.CENTER);
        add(altPanel, BorderLayout.SOUTH);
    }

    private void hesapla() {
        mesajAlani.setText("");
        File anaKlasor = new File(klasorYoluField.getText());
        if (!anaKlasor.exists() || !anaKlasor.isDirectory()) {
            mesajAlani.setText("Geçerli bir klasör seçiniz.");
            return;
        }

        for (File altKlasor : Objects.requireNonNull(anaKlasor.listFiles(File::isDirectory))) {
            Map<String, List<Double>> dosyaVerileri = new HashMap<>();
            List<Double> tumVeriler = new ArrayList<>();

            for (File dosya : Objects.requireNonNull(altKlasor.listFiles((d, name) -> name.endsWith(".txt")))) {
                List<Double> veriler = new ArrayList<>();
                try (BufferedReader reader = new BufferedReader(new FileReader(dosya))) {
                    String satir;
                    while ((satir = reader.readLine()) != null) {
                        try {
                            veriler.add(Double.parseDouble(satir));
                        } catch (NumberFormatException ignored) {}
                    }
                } catch (IOException e) {
                    mesajAlani.append("Hata: " + dosya.getName() + "\n");
                }
                dosyaVerileri.put(dosya.getName(), veriler);
                tumVeriler.addAll(veriler);
            }

            mesajAlani.append("\n>> " + altKlasor.getName() + " klasörü için analiz:\n");

            if (ortalamaCheck.isSelected()) {
                if (ortalamaGlobal.isSelected()) {
                    mesajAlani.append("GLOBAL Ortalama: " + ortalama(tumVeriler) + "\n");
                } else {
                    dosyaVerileri.forEach((ad, liste) -> mesajAlani.append(ad + " Ortalama: " + ortalama(liste) + "\n"));
                }
            }

            if (maxCheck.isSelected()) {
                if (maxGlobal.isSelected()) {
                    mesajAlani.append("GLOBAL Max: " + max(tumVeriler) + "\n");
                } else {
                    dosyaVerileri.forEach((ad, liste) -> mesajAlani.append(ad + " Max: " + max(liste) + "\n"));
                }
            }

            if (minCheck.isSelected()) {
                if (minGlobal.isSelected()) {
                    mesajAlani.append("GLOBAL Min: " + min(tumVeriler) + "\n");
                } else {
                    dosyaVerileri.forEach((ad, liste) -> mesajAlani.append(ad + " Min: " + min(liste) + "\n"));
                }
            }

            if (stdCheck.isSelected()) {
                if (stdGlobal.isSelected()) {
                    mesajAlani.append("GLOBAL Std Sapma: " + stdSapma(tumVeriler) + "\n");
                } else {
                    dosyaVerileri.forEach((ad, liste) -> mesajAlani.append(ad + " Std Sapma: " + stdSapma(liste) + "\n"));
                }
            }

            if (freqCheck.isSelected()) {
                if (freqGlobal.isSelected()) {
                    mesajAlani.append("GLOBAL Frekans: " + frekans(tumVeriler) + "\n");
                } else {
                    dosyaVerileri.forEach((ad, liste) -> mesajAlani.append(ad + " Frekans: " + frekans(liste) + "\n"));
                }
            }

            if (medianCheck.isSelected()) {
                if (medianGlobal.isSelected()) {
                    mesajAlani.append("GLOBAL Median: " + median(tumVeriler) + "\n");
                } else {
                    dosyaVerileri.forEach((ad, liste) -> mesajAlani.append(ad + " Median: " + median(liste) + "\n"));
                }
            }
        }
    }

    private double ortalama(List<Double> liste) {
        return liste.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }

    private double max(List<Double> liste) {
        return liste.stream().mapToDouble(Double::doubleValue).max().orElse(0);
    }

    private double min(List<Double> liste) {
        return liste.stream().mapToDouble(Double::doubleValue).min().orElse(0);
    }

    private double stdSapma(List<Double> liste) {
        double ort = ortalama(liste);
        return Math.sqrt(liste.stream().mapToDouble(d -> Math.pow(d - ort, 2)).average().orElse(0));
    }

    private double median(List<Double> liste) {
        List<Double> sirali = liste.stream().sorted().collect(Collectors.toList());
        int n = sirali.size();
        if (n == 0) return 0;
        return (n % 2 == 1) ? sirali.get(n / 2) : (sirali.get(n / 2 - 1) + sirali.get(n / 2)) / 2.0;
    }

    private Map<Double, Long> frekans(List<Double> liste) {
        return liste.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new IstatistikArayuz().setVisible(true));
    }
}
