package com.tongji.sportmanagement.VenueSubsystem.Entity;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "venue")
public class Venue
{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "venue_id", nullable = false)
  private Integer venueId;

  @Column(name = "name", length = 100)
  private String venueName;

  @Column(name = "description", length = 255)
  private String description;

  @Column(name = "location", length = 100)
  private String location;

  @Enumerated(EnumType.STRING)
  @ColumnDefault("closed")
  @Column(name = "state")
  private VenueState state;
  
  @Column(name = "contact_number")
  private String contactNumber;

  // @Column(name = "image")
  // private String image;

  @Column(name = "manager_id")
  private Integer managerId;
}
