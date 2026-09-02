package example.examplemod.entity

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3

/**
 * Annihilator Turret Entity - Improved Annihilator Energy Cannon
 * 
 * This is an improvement to the Annihilator Energy Cannon with rapid-fire capabilities.
 * Based on SuperbWarfare's GunShootGoal rapid-fire system, adapted for stationary turret use.
 * 
 * Features:
 * - Continuous energy regeneration
 * - Rapid-fire shooting (configurable fire rate)
 * - Energy-based ammo system
 * - Targeting system with spread mechanics
 */
class AnnihilatorTurretEntity(type: EntityType<out AnnihilatorTurretEntity>, world: Level) : Entity(type, world) {
    
    // Firing state
    var isFiring: Boolean = false
    
    // Energy/Ammo
    private var energy by ENERGY
    private var maxEnergy by MAX_ENERGY
    
    // Rapid-fire timing (from SuperbWarfare's GunShootGoal)
    private var shootTimer: Long = 0
    private var lastFireTime: Long = 0
    
    // Targeting
    private var targetX by TARGET_X
    private var targetY by TARGET_Y
    private var targetZ by TARGET_Z
    
    // Configuration (converted from RPM system to RPS - Rounds Per Second)
    var fireRate: Double = 10.0  // Rounds per second (equivalent to RPM / 60)
    var spreadAmount: Float = 2.0f  // Spread in degrees
    var energyPerShot: Int = 5  // Energy cost per shot
    
    init {
        this.noPhysics = true
    }
    
    override fun defineSynchedData() {
        this.entityData.define(ENERGY, 0)
        this.entityData.define(MAX_ENERGY, 100)
        this.entityData.define(TARGET_X, 0.0)
        this.entityData.define(TARGET_Y, 0.0)
        this.entityData.define(TARGET_Z, 0.0)
    }
    
    override fun addAdditionalSaveData(compound: CompoundTag) {
        compound.putInt("Energy", this.energy)
        compound.putInt("MaxEnergy", this.maxEnergy)
        compound.putDouble("TargetX", this.targetX)
        compound.putDouble("TargetY", this.targetY)
        compound.putDouble("TargetZ", this.targetZ)
        compound.putDouble("FireRate", this.fireRate)
        compound.putFloat("Spread", this.spreadAmount)
        compound.putInt("EnergyPerShot", this.energyPerShot)
    }
    
    override fun readAdditionalSaveData(compound: CompoundTag) {
        if (compound.contains("Energy")) this.energy = compound.getInt("Energy")
        if (compound.contains("MaxEnergy")) this.maxEnergy = compound.getInt("MaxEnergy")
        if (compound.contains("TargetX")) this.targetX = compound.getDouble("TargetX")
        if (compound.contains("TargetY")) this.targetY = compound.getDouble("TargetY")
        if (compound.contains("TargetZ")) this.targetZ = compound.getDouble("TargetZ")
        if (compound.contains("FireRate")) this.fireRate = compound.getDouble("FireRate")
        if (compound.contains("Spread")) this.spreadAmount = compound.getFloat("Spread")
        if (compound.contains("EnergyPerShot")) this.energyPerShot = compound.getInt("EnergyPerShot")
    }
    
    override fun tick() {
        super.tick()
        
        if (!this.level().isClientSide) {
            // Energy regeneration
            if (this.energy < this.maxEnergy) {
                this.energy = Math.min(this.energy + 1, this.maxEnergy)
            }
            
            // Rapid-fire logic (adapted from SuperbWarfare's GunShootGoal)
            if (this.isFiring && this.energy > this.energyPerShot) {
                performRapidFire()
            }
        }
    }
    
    /**
     * Rapid-fire shooting logic adapted from SuperbWarfare's GunShootGoal
     * Uses shootTimer with cooldown based on fireRate (RPS - Rounds Per Second)
     * 
     * Original GunShootGoal implementation:
     * - double rps = (double) gunData.get(GunProp.RPM) / 60;
     * - long cooldown = Math.round(1000 / rps);
     * - Fires on every tick if cooldown is met
     */
    private fun performRapidFire() {
        val currentTime = System.currentTimeMillis()
        
        // Calculate cooldown in milliseconds (inverse of fireRate)
        // This converts RPS to milliseconds between shots
        val cooldownMs = (1000.0 / this.fireRate).toLong()
        
        // Initialize timer on first fire
        if (this.shootTimer == 0L) {
            this.shootTimer = currentTime
            this.lastFireTime = currentTime - cooldownMs - 1
        }
        
        // Check if enough time has passed for next shot
        if (currentTime - this.lastFireTime >= cooldownMs) {
            fireShot()
            this.lastFireTime = currentTime
            
            // Consume energy
            this.energy = Math.max(0, this.energy - this.energyPerShot)
        }
    }
    
    /**
     * Fire a single shot
     * Creates a projectile/energy beam towards target position
     * 
     * TODO: Implement actual projectile spawning with spread mechanics
     */
    private fun fireShot() {
        if (this.level().isClientSide) return
        
        val targetVec = Vec3(this.targetX, this.targetY, this.targetZ)
        val turretPos = this.position()
        
        // Calculate direction
        val direction = targetVec.subtract(turretPos).normalize()
        
        // Apply spread (TODO: random spread mechanics)
        // val spreadRad = Math.toRadians(this.spreadAmount.toDouble())
        
        // TODO: Create projectile/energy beam entity here
        // Example: spawn energy projectile with calculated direction and spread
    }
    
    /**
     * Set target position for turret to aim at
     */
    fun setTarget(x: Double, y: Double, z: Double) {
        this.targetX = x
        this.targetY = y
        this.targetZ = z
    }
    
    /**
     * Get current energy level
     */
    fun getEnergy(): Int = this.energy
    
    /**
     * Set energy level
     */
    fun setEnergy(amount: Int) {
        this.energy = Math.min(Math.max(0, amount), this.maxEnergy)
    }
    
    /**
     * Get max energy capacity
     */
    fun getMaxEnergy(): Int = this.maxEnergy
    
    /**
     * Start/stop firing
     */
    fun setFiring(firing: Boolean) {
        this.isFiring = firing
        if (!firing) {
            this.shootTimer = 0L
        }
    }
    
    override fun shouldRenderAtSqrDistance(distance: Double): Boolean = distance < 16384.0

    companion object {
        @JvmField
        val ENERGY: EntityDataAccessor<Int> = SynchedEntityData.defineId(
            AnnihilatorTurretEntity::class.java,
            EntityDataSerializers.INT
        )
        
        @JvmField
        val MAX_ENERGY: EntityDataAccessor<Int> = SynchedEntityData.defineId(
            AnnihilatorTurretEntity::class.java,
            EntityDataSerializers.INT
        )
        
        @JvmField
        val TARGET_X: EntityDataAccessor<Double> = SynchedEntityData.defineId(
            AnnihilatorTurretEntity::class.java,
            EntityDataSerializers.DOUBLE
        )
        
        @JvmField
        val TARGET_Y: EntityDataAccessor<Double> = SynchedEntityData.defineId(
            AnnihilatorTurretEntity::class.java,
            EntityDataSerializers.DOUBLE
        )
        
        @JvmField
        val TARGET_Z: EntityDataAccessor<Double> = SynchedEntityData.defineId(
            AnnihilatorTurretEntity::class.java,
            EntityDataSerializers.DOUBLE
        )
    }
}
