package com.Dash.Dashboard.Entites;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Calendar;
import java.util.Date;


@Data
@Document(collection = "PasswordResetTokens")
public class PasswordResetToken {

    private static final int EXPIRATION_TIME = 5; // Minutes

    @Id
    private String id;

    @NotNull
    @NotEmpty
    private String resetPasswordKey;

    private Date expirationDate;

    private String userId; // LINK to USER ENTITY

    public PasswordResetToken(String userId, String resetPasswordKey) {
        this.resetPasswordKey = resetPasswordKey;
        this.expirationDate = calculateExpirationDate(EXPIRATION_TIME);
        this.userId = userId;
    }

    private Date calculateExpirationDate(int expirationTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE, expirationTime);
        return new Date(calendar.getTime().getTime());
    }

}
