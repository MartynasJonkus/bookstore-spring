package com.bookstore.mappers;

import com.bookstore.model.Book;
import com.bookstore.model.Publisher;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PublisherMapper {

    @Select("SELECT * FROM publishers")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "address", column = "address"),
            @Result(property = "books", column = "id", many = @Many(select = "getBooksByPublisher"))
    })
    List<Publisher> findAll();

    @Select("SELECT * FROM publishers WHERE id = #{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "address", column = "address"),
            @Result(property = "books", column = "id", many = @Many(select = "getBooksByPublisher"))
    })
    Publisher findById(Long id);

    @Insert("INSERT INTO publishers (name, address) VALUES (#{name}, #{address})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Publisher publisher);

    @Update("UPDATE publishers SET name = #{name}, address = #{address} WHERE id = #{id}")
    void update(Publisher publisher);

    @Delete("DELETE FROM publishers WHERE id = #{id}")
    void deleteById(Long id);

    @Select("SELECT * FROM books WHERE publisher_id = #{publisherId}")
    List<Book> getBooksByPublisher(Long publisherId);
}
