package com.siti.email_service.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

@Service
public class EmailSenderService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String productName, int quantity, BigDecimal totalPrice) {
        try {
            NumberFormat rupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            String formattedTotal = rupiah.format(totalPrice);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Konfirmasi Pesanan Anda");

            String html =
                "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px;'>" +
                "  <div style='background-color: #4CAF50; padding: 20px; text-align: center; border-radius: 8px 8px 0 0;'>" +
                "    <h2 style='color: white; margin: 0;'>Pesanan Berhasil!</h2>" +
                "  </div>" +
                "  <div style='background-color: #f9f9f9; padding: 20px; border: 1px solid #ddd;'>" +
                "    <p style='font-size: 16px;'>Halo,</p>" +
                "    <p style='font-size: 16px;'>Terima kasih telah berbelanja! Pesanan Anda telah berhasil dibuat.</p>" +
                "    <table style='width: 100%; border-collapse: collapse; margin-top: 20px;'>" +
                "      <thead>" +
                "        <tr style='background-color: #4CAF50; color: white;'>" +
                "          <th style='padding: 12px; text-align: left; border: 1px solid #ddd;'>Keterangan</th>" +
                "          <th style='padding: 12px; text-align: left; border: 1px solid #ddd;'>Detail</th>" +
                "        </tr>" +
                "      </thead>" +
                "      <tbody>" +
                "        <tr style='background-color: #ffffff;'>" +
                "          <td style='padding: 12px; border: 1px solid #ddd;'>Produk</td>" +
                "          <td style='padding: 12px; border: 1px solid #ddd;'>" + productName + "</td>" +
                "        </tr>" +
                "        <tr style='background-color: #f2f2f2;'>" +
                "          <td style='padding: 12px; border: 1px solid #ddd;'>Jumlah</td>" +
                "          <td style='padding: 12px; border: 1px solid #ddd;'>" + quantity + " item</td>" +
                "        </tr>" +
                "        <tr style='background-color: #ffffff;'>" +
                "          <td style='padding: 12px; border: 1px solid #ddd;'>Total Harga</td>" +
                "          <td style='padding: 12px; border: 1px solid #ddd;'><strong>" + formattedTotal + "</strong></td>" +
                "        </tr>" +
                "        <tr style='background-color: #f2f2f2;'>" +
                "          <td style='padding: 12px; border: 1px solid #ddd;'>Status</td>" +
                "          <td style='padding: 12px; border: 1px solid #ddd;'><span style='color: #4CAF50; font-weight: bold;'>Success</span></td>" +
                "        </tr>" +
                "      </tbody>" +
                "    </table>" +
                "  </div>" +
                "  <div style='background-color: #eeeeee; padding: 15px; text-align: center; border-radius: 0 0 8px 8px; border: 1px solid #ddd;'>" +
                "    <p style='margin: 0; font-size: 14px; color: #666;'>Jika ada pertanyaan, silakan balas email ini.</p>" +
                "    <p style='margin: 5px 0 0; font-size: 14px; color: #666;'>Salam, <strong>Tim Toko Online</strong></p>" +
                "  </div>" +
                "</div>";

            helper.setText(html, true);
            mailSender.send(message);

        } catch (Exception e) {
            System.err.println("Gagal kirim email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
