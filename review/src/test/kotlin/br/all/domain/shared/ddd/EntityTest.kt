package br.all.domain.shared.ddd

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class EntityTest{

    @JvmInline
   private value class EntityId(val value: Long) : Identifier {
        override fun validate() = Notification ()
    }

    private class EntityA(id: EntityId) : Entity (id)
    private class EntityB(id: EntityId) : Entity (id)

    @Test
    fun `Should two entities of same class and id be equal`(){
        val entityA1 = EntityA(EntityId(10L))
        val entityA2 = EntityA(EntityId(10L))
        assertEquals(entityA1, entityA2)
    }

    @Test
    fun `Should two entities of same class and different ids be different`(){
        val entityA1 = EntityA(EntityId(10L))
        val entityA2 = EntityA(EntityId(11L))
        assertNotEquals(entityA1, entityA2)
    }

    @Test
    fun `Should an entity be different from a not entity`(){
        data class OtherClass(val id: Long)
        val otherObject = OtherClass(10L);
        val entityA = EntityA(EntityId(10L))
        assertNotEquals(otherObject, entityA)
    }

    @Test
    fun `Should two entities of different classes and same index be different`(){
        val entityA = EntityA(EntityId(10L))
        val entityB = EntityB(EntityId(10L))
        assertNotEquals(entityA, entityB)
    }

    @Test
    fun `Should entity be different from null`(){
        val entityA = EntityA(EntityId(10L))
        assertFalse(entityA.equals(null));
    }

    @Test
    fun `Should two variables of same object be equal`(){
        val entityA = EntityA(EntityId(10L))
        val entityB = entityA
        assertEquals(entityA, entityB)
    }
}