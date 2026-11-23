package com.example;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;

public class QrCodeGenerator {

    /**
     * Encodes the given text as a QR code and writes it to a PNG file.
     *
     * @param text       the string to encode
     * @param size       width/height of the image in pixels (it is square)
     * @param outputPath filename or relative path, resolved inside the outputs folder
     */
    public static Path generateQrCode(String text, int size, String outputPath)
            throws WriterException, IOException {

        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 2); // quiet zone, in modules

        BitMatrix matrix = new QRCodeWriter()
                .encode(text, BarcodeFormat.QR_CODE, size, size, hints);

        BufferedImage image = new BufferedImage(
                matrix.getWidth(), matrix.getHeight(), BufferedImage.TYPE_INT_RGB);

        int black = Color.BLACK.getRGB();
        int white = Color.WHITE.getRGB();

        for (int x = 0; x < matrix.getWidth(); x++) {
            for (int y = 0; y < matrix.getHeight(); y++) {
                image.setRGB(x, y, matrix.get(x, y) ? black : white);
            }
        }

        Path outputFile = Path.of(outputPath);
        if (!outputFile.isAbsolute()) {
            Path outputsFolder = Path.of("outputs");
            outputFile = outputFile.startsWith(outputsFolder)
                    ? outputFile
                    : outputsFolder.resolve(outputFile);
        }

        Path parent = outputFile.toAbsolutePath().normalize().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        if (!ImageIO.write(image, "png", outputFile.toFile())) {
            throw new IOException("No image writer is available for PNG files");
        }

        return outputFile;
    }

    private static final int DEFAULT_SIZE = 400;
    public static void main(String[] args) throws Exception {
        if(args.length == 0){
            System.err.println("Usage Error: Please Include the Text to be Encoded");
            return;
        }

        String text = args[0];
        int size = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_SIZE;
        String outputPath = args.length > 2 ? args[2] : "qrcode.png";

        Path savedPath = generateQrCode(text, size, outputPath).toAbsolutePath().normalize();
        System.out.println("Saved to " + savedPath);
    }
}