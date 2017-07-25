package test.grails.appinfo

class Book {

    Date dateCreated
    Date lastUpdated

    String title
    String isbn
    String author
    String summary


    static constraints = {
        isbn nullable: true
        author nullable: true
        summary nullable: true
    }


}
