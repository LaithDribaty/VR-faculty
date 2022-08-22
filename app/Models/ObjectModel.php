<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class ObjectModel extends Model
{
    use HasFactory;
    protected $fillable = [
        'position',
        'rotation',
        'size',
        'texture_id',
        'mesh_id',
        'house_id'
    ];

    /**
     * The table associated with the model.
     *
     * @var string
     */
    protected $table = 'objects';

    public function house()
    {
        return $this->belongsTo(House::class);
    }

    // if null then it is a light
    public function mesh()
    {
        return $this->belongsTo(Mesh::class);
    }

    public function texture()
    {
        return $this->belongsTo(Texture::class);
    }

    // override the toArray function (called by toJson)
    public function toArray() {
        // get the original array to be displayed
        $data = parent::toArray();

        if($this->mesh) {
            $data['meshName'] = $this->mesh->name;
        }

        return $data;
    }
}
