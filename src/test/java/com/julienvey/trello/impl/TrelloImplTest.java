package com.julienvey.trello.impl;

import com.julienvey.trello.domain.Card;
import com.julienvey.trello.impl.http.ApacheHttpClient;
import org.junit.Ignore;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TrelloImplTest {

    private final static String APPLICATION_KEY = "59f07843489f82c00db8ae8a4352b431";
    private static final String TOKEN = "922237e2f805966fdd75f80530cd0c113ba419c40f1b25878fa447ae03751662";

    @Ignore
    @Test
    public void createCard_for_manual_use() {
        final Card card = new Card();
        card.setName("Name for test");
        card.setDesc("Description for test");
        final TrelloImpl trello = getTrello();
        final Card newCard = trello.createCard("53f79d49860cd5f7c2dd144d", card);
        trello.addAttachmentToCard(newCard.getId(), jpgByteArray());
    }

    @Ignore("For manual use")
    @Test
    public void addAttachmentToCard_for_manual_use() {
        getTrello().addAttachmentToCard("53faf8696a7a4c37d2afa7fc", jpgByteArray());
    }

    private TrelloImpl getTrello() {
        return new TrelloImpl(APPLICATION_KEY, TOKEN, new ApacheHttpClient());
    }

    private static byte[] jpgByteArray() {
        try {
            final BufferedImage bufferedImage = ImageIO.read(TrelloImplTest.class.getResourceAsStream("cute-kittens-in-cups-pics.jpg"));
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpeg", outputStream);
            outputStream.flush();
            System.out.println(outputStream.size());
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

}