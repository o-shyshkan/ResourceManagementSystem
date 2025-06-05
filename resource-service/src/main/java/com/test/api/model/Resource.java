package com.test.api.model;


import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "resources")
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private ResourceType resourceType;
    private String countryCode;
    @Cascade({CascadeType.REFRESH, CascadeType.MERGE, CascadeType.PERSIST})
    @ManyToOne
    private Location location;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "resources_characteristics",
            joinColumns = @JoinColumn(name = "resource_id"),
            inverseJoinColumns = @JoinColumn(name = "characteristic_id"))
    @Cascade({CascadeType.REFRESH, CascadeType.MERGE, CascadeType.PERSIST})
    private List<Characteristic> Characteristics;
}
