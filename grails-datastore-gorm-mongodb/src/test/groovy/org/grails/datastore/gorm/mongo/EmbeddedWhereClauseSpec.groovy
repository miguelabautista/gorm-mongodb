package org.grails.datastore.gorm.mongo

import grails.gorm.services.Service
import grails.gorm.services.Where
import grails.gorm.tests.GormDatastoreSpec
import grails.persistence.Entity
import org.springframework.beans.factory.annotation.Autowired

import javax.persistence.Embeddable

class EmbeddedWhereClauseSpec extends GormDatastoreSpec {

    @Autowired
    PersonAttributeDataService personAttributeDataService

    void "Can construct data service where clause on embedded object"() {
        given:"An object with an embedded field on it"
        def attribute = new PersonAttribute(contexts: [new AttributeContext(neighborhoodId: '1234')])
        attribute.save()

        when:"We query using the autogenerated where clause"
        def response = personAttributeDataService.findByNeighborhoodId('1234')

        then:"The association is valid"
        response.size() == 1
        response.first().contexts.first().neighborhoodId == '1234'

    }

    @Override
    List getDomainClasses() {
        [PersonAttribute]
    }
}

@Entity
class PersonAttribute {
    String id
    List<AttributeContext> contexts = []
    static embedded = ['contexts']
}

@Embeddable
class AttributeContext {

    String id
    String neighborhoodId

    static belongsTo = [attribute: PersonAttribute]

}

@Service(PersonAttribute)
interface PersonAttributeDataService {
    @Where({ contexts { neighborhoodId == neighborhoodId } })
    List<PersonAttribute> findByNeighborhoodId(String neighborhoodId)
}
