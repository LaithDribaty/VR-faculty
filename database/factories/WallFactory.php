<?php

namespace Database\Factories;

use Illuminate\Database\Eloquent\Factories\Factory;

class WallFactory extends Factory
{
    /**
     * Define the model's default state.
     *
     * @return array
     */
    public function definition()
    {
        return [
            'thick' => 3,
            'start' => json_encode([
                'x' => rand(1, 200),
                'y' => rand(1, 200)
            ]),
            'end' => json_encode([
                'x' => rand(1, 200),
                'y' => rand(1, 200)
            ]),
            'house_id' => 2
        ];
    }
}
