package com.example.orchid.pojos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "orchids")
public class Orchid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orchidID;

    @NotBlank
    @Size(max = 150)
    @Nationalized
    @Column(nullable = false, length = 150)
    private String orchidName;

    private Boolean isNatural;

    @Size(max = 1000)
    @Nationalized
    @Column(length = 1000)
    private String orchidDescription;

    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private OrchidCategory orchidCategory;

    private Boolean isAttractive;

    @Size(max = 255)
    private String orchidURL;

    public Orchid() {}

    public Orchid(Long orchidID, String orchidName, Boolean isNatural, String orchidDescription,
                  OrchidCategory orchidCategory, Boolean isAttractive, String orchidURL) {
        this.orchidID = orchidID;
        this.orchidName = orchidName;
        this.isNatural = isNatural;
        this.orchidDescription = orchidDescription;
        this.orchidCategory = orchidCategory;
        this.isAttractive = isAttractive;
        this.orchidURL = orchidURL;
    }

    public Long getOrchidID() {
        return orchidID;
    }

    public void setOrchidID(Long orchidID) {
        this.orchidID = orchidID;
    }

    public String getOrchidName() {
        return orchidName;
    }

    public void setOrchidName(String orchidName) {
        this.orchidName = orchidName;
    }

    public Boolean getIsNatural() {
        return isNatural;
    }

    public void setIsNatural(Boolean natural) {
        isNatural = natural;
    }

    public String getOrchidDescription() {
        return orchidDescription;
    }

    public void setOrchidDescription(String orchidDescription) {
        this.orchidDescription = orchidDescription;
    }

    public OrchidCategory getOrchidCategory() {
        return orchidCategory;
    }

    public void setOrchidCategory(OrchidCategory orchidCategory) {
        this.orchidCategory = orchidCategory;
    }

    public Boolean getIsAttractive() {
        return isAttractive;
    }

    public void setIsAttractive(Boolean attractive) {
        isAttractive = attractive;
    }

    public String getOrchidURL() {
        return orchidURL;
    }

    public void setOrchidURL(String orchidURL) {
        this.orchidURL = orchidURL;
    }
}
