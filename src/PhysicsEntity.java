import javafx.scene.shape.Shape;

import java.util.List;

public class PhysicsEntity implements GameConstants {
    private Vector2 position;
    private Vector2 velocity;
    public CollidableRect hitBox;
    private final List<List<Tile>> tileMatrix;
    public boolean wasTouchingGround = false;

    public PhysicsEntity(Vector2 position, Vector2 velocity, CollidableRect hitBox, List<List<Tile>> tileMatrix) {
        this.setPosition(position);
        this.setVelocity(velocity);
        this.hitBox = hitBox;
        this.tileMatrix = tileMatrix;
    }

    boolean isTouchingTerrain() {
        // TODO make this work for any size entity, efficient
        int i = 0;
        for (List<Tile> row : tileMatrix) {
            int j = 0;
            for (Tile tile : row) {
                // don't detect for slopes
                if (tile == null || tile.isSlope) {
                    j++;

                    continue;
                }

                for (CollidableShape tileFixtureShape : tile.fixtureShapes) {
                    hitBox.setPosition(this.position);
                    if (tileFixtureShape.intersectsWith(hitBox)) {
                        return true;
                    }
                }
                j++;

            }
            i++;
        }
        return false;
    }

    boolean isTouchingSlope() {
        // TODO make this work for any size entity, efficient
        int i = 0;
        for (List<Tile> row : tileMatrix) {
            int j = 0;
            for (Tile tile : row) {
                if (tile == null || !tile.isSlope) {
                    j++;

                    continue;
                }

                for (CollidableShape tileFixtureShape : tile.fixtureShapes) {
                    hitBox.setPosition(this.position);
                    if (tileFixtureShape.intersectsWith(hitBox)) {
                        return true;
                    }
                }
                j++;

            }
            i++;
        }
        return false;
    }

    boolean isPointTouchingTerrain(Vector2 point) {
        int row = (int) -Math.floor(point.y) - 1;
        int col = (int) Math.floor(point.x);
        Tile tile = null;
        try {
            tile = tileMatrix.get(row).get(col);
        } catch (IndexOutOfBoundsException e) {
            // tile doesn't exist in map
        }
        if (tile == null || tile.isSlope) return false;

        for (CollidableShape shape : tile.fixtureShapes) {
            if (shape.isPointInside(point)) {
                return true;
            }
        }
        return false;
    }

    boolean isPointTouchingSlope(Vector2 point) {
        int row = (int) -Math.floor(point.y) - 1;
        int col = (int) Math.floor(point.x);
        Tile tile = tileMatrix.get(row).get(col);
        if (tile == null || !tile.isSlope) return false;

        for (CollidableShape shape : tile.fixtureShapes) {
            if (shape.isPointInside(point)) {
                return true;
            }
        }
        return false;
    }

    void move(double timeDeltaSeconds) {
        final double floatAmount = 0.001;
        boolean wasTouchingSlope = false;

        position.y += velocity.y * timeDeltaSeconds;

        wasTouchingGround = false;
        if (isTouchingTerrain()) {
            // find out which side
            if (isPointTouchingTerrain(new Vector2(position.x - hitBox.width / 2, position.y + hitBox.height / 2))
                    || isPointTouchingTerrain(new Vector2(position.x + hitBox.width / 2, position.y + hitBox.height / 2))) {
                // correct down
                // ASSUMES tile, doesn't work for complex shapes
                position.y = Math.ceil(position.y) - hitBox.height / 2 - floatAmount;
            } else if (isPointTouchingTerrain(new Vector2(position.x - hitBox.width / 2, position.y - hitBox.height / 2))
                    || isPointTouchingTerrain(new Vector2(position.x + hitBox.width / 2, position.y - hitBox.height / 2))) {
                // correct up
                // ASSUMES tile, doesn't work for complex shapes
                position.y = Math.floor(position.y) + hitBox.height / 2 + floatAmount;
                wasTouchingGround = true;
            } else {
                System.out.println("I don't think this should happen, since it should be some side that's hitting");
            }
            velocity.y = 0;
        }

        if (isTouchingSlope()) {
            wasTouchingGround = true;
            wasTouchingSlope = true;
            velocity.x *= 0.95;
            int maxTries = 100;
            while (maxTries-- > 0 && isPointTouchingSlope(new Vector2(position.x, position.y - hitBox.height / 2))) {
                position.y += 0.005;
                velocity.y = 0;
            }
        }


        position.x += velocity.x * timeDeltaSeconds;

        if (isTouchingTerrain() && !wasTouchingSlope) {
            // find out which side
            if (isPointTouchingTerrain(new Vector2(position.x + hitBox.width / 2, position.y - hitBox.height / 2))
                    || isPointTouchingTerrain(new Vector2(position.x + hitBox.width / 2, position.y))
                    || isPointTouchingTerrain(new Vector2(position.x + hitBox.width / 2, position.y + hitBox.height / 2))) {
                // hitting right side, correct left
                // ASSUMES tile, doesn't work for complex shapes
                position.x = Math.ceil(position.x) - hitBox.width / 2 - floatAmount;
            } else if (isPointTouchingTerrain(new Vector2(position.x - hitBox.width / 2, position.y - hitBox.height / 2))
                    || isPointTouchingTerrain(new Vector2(position.x - hitBox.width / 2, position.y))
                    || isPointTouchingTerrain(new Vector2(position.x - hitBox.width / 2, position.y + hitBox.height / 2))) {
                // hitting left side, correct right
                // ASSUMES tile, doesn't work for complex shapes
                position.x = Math.floor(position.x) + hitBox.width / 2 + floatAmount;
            }
            velocity.x = 0;
        }

        if (wasTouchingGround) {
            velocity.x *= GROUND_FRICTION;
        } else {
            velocity.x *= AIR_FRICTION;
        }
        velocity.y *= AIR_FRICTION;

        //
//        if (isTouchingTerrain()) {
//
//            // is this a slope? try to move up
//            double maxSlopePerBlock = 1; // allow 45 deg angle and less
//            double increase = 0;
//            double initY = position.y;
//            while (isTouchingTerrain() && increase <= maxSlopePerBlock * Math.abs(velocity.x * timeDeltaSeconds)) {
//                increase += Math.abs(position.x) / 10;
//                position.y = initY + increase;
//                System.out.println("Trying to move up, increase is " + increase + " out of " + Math.abs(velocity.x));
//            }
//
//            if (increase > maxSlopePerBlock) {
//                // unsuccessful slope, move back instead
//                position.y = initY;
//
//                // move in opposite direction till not touching
//                int maxTries = 100;
//                Vector2 opposite = new Vector2(velocity.x / -velocity.getMagnitude() / maxTries, velocity.y / -velocity.getMagnitude() / maxTries);
//                System.out.println("Opposite is " + opposite);
//                while (isTouchingTerrain()) {
//                    position.x += opposite.x;
//                    position.y += opposite.y;
//                    if (--maxTries < 0) {
//                        break;
//                    }
//                }
//
//                double damping = 0;// 0.3;
//                velocity.x *= -damping;
//                velocity.y *= -damping;
//            }
//        }
    }

    public void setPosition(Vector2 newPos) {
        this.position = new Vector2(newPos);
    }

    public void setVelocity(Vector2 newVel) {
        this.velocity = new Vector2(newVel);
    }

    public final Vector2 getPosition() {
        return new Vector2(position);
    }

    public final Vector2 getVelocity() {
        return this.velocity;
    }

    public final CollidableRect getHitBox() {
        return this.hitBox;
    }
}
