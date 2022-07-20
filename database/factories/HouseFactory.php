<?php

namespace Database\Factories;

use App\Models\User;
use Illuminate\Database\Eloquent\Factories\Factory;

class HouseFactory extends Factory
{
    /**
     * Define the model's default state.
     *
     * @return array
     */
    public function definition()
    {
        return [
            'location' => $this->faker->name(),
            'description' => $this->faker->paragraph(),
            'price' => rand(1, 2000),
            'size' => rand(60, 300),
            'user_id' => rand(1, User::all()->count())
        ];
    }
}
