package com.demo.docker;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PersonDto {
    private Long id;
    private String name;

    @Override
    public String toString() {
        return "PersonDto{" +
               "id=" + id +
               ", name='" + name + '\'' +
               '}';
    }
}