package billennium.quizapp

import spock.lang.Specification

class HelloSpec extends Specification {

    void "testing spock works"(){
        expect:
        true
    }

    def "one plus one should equal two"() {
        expect:
        1 + 1 == 2
    }
}
